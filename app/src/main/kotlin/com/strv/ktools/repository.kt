package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import retrofit2.Response

/**
 * Resource wrapper adding status to its value
 */
data class Resource<T>(
	val status: Status,
	val data: T? = null,
	val message: String? = null
) {
	enum class Status { SUCCESS, ERROR, LOADING }
}

/**
 * BaseClass for making any resource accessible via LiveData interface with database cache support
 */
open class ResourceLiveData<T> : MediatorLiveData<Resource<T>>() {
	private val resource = NetworkBoundResource(this)

	fun setupResource(resourceCallback: NetworkBoundResource.Callback<T>) {
		resource.setup(resourceCallback)
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

		// Called with the data in the database to decide whether it should be
		// fetched from the network.
		@MainThread
		fun shouldFetch(data: T?): Boolean

		// Called to get the cached data from the database
		@MainThread
		fun loadFromDb(): LiveData<T>

		// Called to create the API call.
		@MainThread
		fun createCall(): LiveData<Response<T>>

		// Called when the fetch fails. The child class may want to reset components
		// like rate limiter.
		@MainThread
		fun onFetchFailed() {
		}
	}

	private lateinit var callback: Callback<T>
	private val savedSources = mutableSetOf<LiveData<*>>()

	init {
		result.value = Resource(Resource.Status.LOADING, null)
	}

	fun setup(resourceCallback: Callback<T>) {
		callback = resourceCallback

		// clear saved sources from previous setup
		savedSources.forEach { result.removeSource(it) }
		savedSources.clear()

		result.value = Resource(result.value?.status
			?: Resource.Status.LOADING, result.value?.data, result.value?.message)

		val dbSource = callback.loadFromDb()
		savedSources.add(dbSource)
		result.addSource(dbSource, { data ->
			savedSources.remove(dbSource)
			result.removeSource(dbSource)
			if (callback.shouldFetch(data)) {
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
		val apiResponse = callback.createCall()
		// we re-attach dbSource as a new source,
		// it will dispatch its latest value quickly
		savedSources.add(dbSource)
		result.addSource(dbSource, { newData -> result.setValue(Resource(Resource.Status.LOADING, newData)) })
		savedSources.add(apiResponse)
		result.addSource(apiResponse, { response ->
			savedSources.remove(apiResponse)
			result.removeSource(apiResponse)
			savedSources.remove(dbSource)
			result.removeSource(dbSource)

			if (response != null && response.isSuccessful) {
				saveResultAndReInit(response)
			} else {
				callback.onFetchFailed()
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.ERROR, newData, response?.message()))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(response: Response<T>) {
		doAsync {
			callback.saveCallResult(response.body()!!)
			uiThread {
				val dbSource = callback.loadFromDb()
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.SUCCESS, newData))
				})
			}
		}
	}
}