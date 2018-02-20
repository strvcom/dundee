package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import retrofit2.Response

/**
 * Resource wrapper adding status and error to its value
 */
data class Resource<T> constructor(
	val status: Status,
	val data: T? = null,
	val message: String? = null,
	val rawResponse: Response<T>? = null,
	val throwable: Throwable? = null
) {
	enum class Status { SUCCESS, ERROR, FAILURE, NO_CONNECTION, LOADING }
	companion object {
		fun <T> fromResponse(response: Response<T>?, error: Throwable?): Resource<T> {
			val message = response?.message() ?: error?.message
			var status = Resource.Status.SUCCESS
			if (response == null || response.isSuccessful.not()) {
				status = if (response != null) Resource.Status.ERROR else if (error is NoConnectivityException) Resource.Status.NO_CONNECTION else Resource.Status.FAILURE
			}
			return Resource(status, response?.body(), message, response, error)
		}

		fun <T> loading(data: T? = null, message: String? = null) = Resource(Status.LOADING, data, message, null, null)
		fun <T> success(data: T?, message: String? = null) = Resource(Status.SUCCESS, data, message, null, null)
		fun <T> error(throwable: Throwable?, message: String? = throwable?.message) = Resource(Status.ERROR, null, message, null, throwable)
	}

	fun <S> map(mapFunction: (T?) -> S?) = Resource(status, mapFunction(data), message, rawResponse?.map(mapFunction), throwable)
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
	private val resource = NetworkBoundResource(this)

	fun setupCached(resourceCallback: NetworkBoundResource.Callback<T>) {
		resource.setupCached(resourceCallback)
	}

	fun setup(networkCallLiveData: LiveData<Resource<T>>) {
		resource.setup(networkCallLiveData)
	}
}

// -- internal --

/**
 * NetworkBoundResource based on https://developer.android.com/topic/libraries/architecture/guide.html, but modified
 * Note: use Call<T>.map() extension function to map Retrofit response to the entity object - therefore we don't need RequestType and ResponseType separately
 */
class NetworkBoundResource<T>(private val result: ResourceLiveData<T>) {
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
		fun createNetworkCall(): LiveData<Resource<T>>
	}

	private var callback: Callback<T>? = null
	private val savedSources = mutableSetOf<LiveData<*>>()

	init {
		result.value = Resource.loading()
	}

	fun setup(networkCallLiveData: LiveData<Resource<T>>) {
		callback = null

		// clear saved sources from previous setup
		savedSources.forEach { result.removeSource(it) }
		savedSources.clear()

		result.value = result.value?.copy(status = result.value?.status ?: Resource.Status.LOADING) ?: Resource.loading()

		result.addSource(networkCallLiveData, { networkResource ->
			result.setValue(networkResource)
		})
	}

	fun setupCached(resourceCallback: Callback<T>) {
		callback = resourceCallback

		// clear saved sources from previous setup
		savedSources.forEach { result.removeSource(it) }
		savedSources.clear()

		result.value = result.value?.copy(status = result.value?.status ?: Resource.Status.LOADING) ?: Resource.loading()

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
					result.setValue(Resource.success(newData))
				})
			}
		})
	}

	private fun fetchFromNetwork(dbSource: LiveData<T>) {
		val apiResponse = callback!!.createNetworkCall()
		// we re-attach dbSource as a new source,
		// it will dispatch its latest value quickly
		savedSources.add(dbSource)
		result.addSource(dbSource, { newData -> result.setValue(Resource.loading(newData)) })
		savedSources.add(apiResponse)
		result.addSource(apiResponse, { networkResource ->
			savedSources.remove(apiResponse)
			result.removeSource(apiResponse)
			savedSources.remove(dbSource)
			result.removeSource(dbSource)

			if (networkResource?.status == Resource.Status.SUCCESS) {
				saveResultAndReInit(networkResource)
			} else {
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(networkResource?.copy(data = newData))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(resource: Resource<T>) {
		doAsync {
			callback?.let {
				resource.data?.let { callback!!.saveCallResult(it) }
				uiThread {
					val dbSource = callback!!.loadFromDb()
					savedSources.add(dbSource)
					result.addSource(dbSource, { newData ->
						result.setValue(resource.copy(data = newData))
					})
				}
			}
		}
	}
}
