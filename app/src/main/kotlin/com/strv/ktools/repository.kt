package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Resource wrapper adding status and error to its value
 */
data class Resource<T>(
	val status: Status,
	val data: T? = null,
	val message: String? = null,
	val statusCode: Int? = null,
	val error: Error? = null
) {
	enum class Status { SUCCESS, ERROR, FAILURE, NO_CONNECTION, LOADING }
}

/**
 * Error entity
 */
data class Error(
	val errorCode: Int
)

/**
 * BaseClass for making any resource accessible via LiveData interface with database cache support
 */
open class ResourceLiveData<T> : MediatorLiveData<Resource<T>>() {
	private val resource = CachedNetworkBoundResource(this)

	fun setupCached(resourceCallback: CachedNetworkBoundResource.Callback<T>) {
		resource.setupCached(resourceCallback)
	}

	fun setup(networkCallLiveData: LiveData<RetrofitResponse<T>>) {
		resource.setup(networkCallLiveData)
	}
}

// -- internal --

/**
 * CachedNetworkBoundResource based on https://developer.android.com/topic/libraries/architecture/guide.html, but modified
 * Note: use Call<T>.map() extension function to map Retrofit response to the entity object - therefore we don't need RequestType and ResponseType separately
 */
class CachedNetworkBoundResource<T>(private val result: ResourceLiveData<T>) {
	interface Callback<T> {
		// Called to save the result of the API response into the database
		@WorkerThread
		fun saveCallResult(item: T)

		// Called with the dataFromCache in the database to decide whether it should be
		// fetched from the network.
		@MainThread
		fun shouldFetch(dataFromCache: T?): Boolean

		// Called to get the cached data from the database
		@MainThread
		fun loadFromDb(): LiveData<T>

		// Called to create the API call.
		@MainThread
		fun createNetworkCall(): LiveData<RetrofitResponse<T>>

		// Called when the fetch fails. The child class may want to reset components
		// like rate limiter.
		@MainThread
		fun onFetchFailed(status: Resource.Status, statusCode: Int?) {
		}
	}

	private var callback: Callback<T>? = null
	private val savedSources = mutableSetOf<LiveData<*>>()

	init {
		result.value = Resource(Resource.Status.LOADING, null)
	}

	fun setup(networkCallLiveData: LiveData<RetrofitResponse<T>>) {
		callback = null

		// clear saved sources from previous setup
		savedSources.forEach { result.removeSource(it) }
		savedSources.clear()

		result.value = Resource(result.value?.status
			?: Resource.Status.LOADING, result.value?.data, result.value?.message)

		result.addSource(networkCallLiveData, { retrofitResponse ->
			val status = if (retrofitResponse?.response != null) Resource.Status.ERROR else if (retrofitResponse?.throwable is NoConnectivityException) Resource.Status.NO_CONNECTION else Resource.Status.FAILURE
			val message = retrofitResponse?.response?.message() ?: retrofitResponse?.throwable?.message
			result.setValue(Resource(status, retrofitResponse?.response?.body(), message, retrofitResponse?.response?.code(), parseError(retrofitResponse?.response)))
		})
	}

	fun setupCached(resourceCallback: Callback<T>) {
		callback = resourceCallback

		// clear saved sources from previous setup
		savedSources.forEach { result.removeSource(it) }
		savedSources.clear()

		result.value = Resource(result.value?.status
			?: Resource.Status.LOADING, result.value?.data, result.value?.message)

		val dbSource = callback!!.loadFromDb()
		savedSources.add(dbSource)
		result.addSource(dbSource, { data ->
			savedSources.remove(dbSource)
			result.removeSource(dbSource)
			if (callback!!.shouldFetch(data)) {
				fetchFromNetwork(dbSource)
			} else {
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.SUCCESS, newData))
				})
			}
		})
	}

	private fun fetchFromNetwork(dbSource: LiveData<T>) {
		val apiResponse = callback!!.createNetworkCall()
		// we re-attach dbSource as a new source,
		// it will dispatch its latest value quickly
		savedSources.add(dbSource)
		result.addSource(dbSource, { newData -> result.setValue(Resource(Resource.Status.LOADING, newData)) })
		savedSources.add(apiResponse)
		result.addSource(apiResponse, { retrofitResponse ->
			savedSources.remove(apiResponse)
			result.removeSource(apiResponse)
			savedSources.remove(dbSource)
			result.removeSource(dbSource)

			if (retrofitResponse?.response?.isSuccessful == true) {
				saveResultAndReInit(retrofitResponse.response)
			} else {
				val status = if (retrofitResponse?.response != null) Resource.Status.ERROR else if (retrofitResponse?.throwable is NoConnectivityException) Resource.Status.NO_CONNECTION else Resource.Status.FAILURE
				val message = retrofitResponse?.response?.message() ?: retrofitResponse?.throwable?.message
				callback?.onFetchFailed(status, retrofitResponse?.response?.code())
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(status, newData, message, retrofitResponse?.response?.code(), parseError(retrofitResponse?.response)))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(response: Response<T>) {
		doAsync {
			callback?.let {
				callback!!.saveCallResult(response.body()!!)
				uiThread {
					val dbSource = callback!!.loadFromDb()
					savedSources.add(dbSource)
					result.addSource(dbSource, { newData ->
						result.setValue(Resource(Resource.Status.SUCCESS, newData))
					})
				}
			}
		}
	}

	private fun parseError(response: Response<T>?): Error? {
		return try {
			val converter = GsonConverterFactory.create(GsonBuilder().create()).responseBodyConverter(Error::class.java, arrayOfNulls(0), null)
			converter?.convert(response?.errorBody()) as Error
		} catch (e: IOException) {
			getGenericError()
		} catch (e: NullPointerException) {
			getGenericError()
		} catch (e: JsonSyntaxException) {
			getGenericError()
		}
	}

	private fun getGenericError() = Error(0)
}
