package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import retrofit2.Response

class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {
	enum class Status { SUCCESS, ERROR, LOADING }
}

// ResultType: Type for the Resource data
// RequestType: Type for the API response
class NetworkBoundResource<ResultType, RequestType>(val result: ResourceLiveData<ResultType, RequestType>) {

	interface Callback<ResultType, RequestType> {
		// Called to save the result of the API response into the database
		@WorkerThread
		fun saveCallResult(item: RequestType)

		// Called with the data in the database to decide whether it should be
		// fetched from the network.
		@MainThread
		fun shouldFetch(data: ResultType?): Boolean

		// Called to get the cached data from the database
		@MainThread
		fun loadFromDb(): LiveData<ResultType>

		// Called to create the API call.
		@MainThread
		fun createCall(): LiveData<Response<RequestType>>
	}

	private lateinit var callback: Callback<ResultType, RequestType>
	private val savedSources = mutableSetOf<LiveData<*>>()

	init {
		result.value = Resource(Resource.Status.LOADING, null)
	}

	fun setup(resourceCallback: Callback<ResultType, RequestType>) {
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

	private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
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
				onFetchFailed()
				savedSources.add(dbSource)
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.ERROR, newData, response?.message()))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(response: Response<out RequestType>) {
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

	// Called when the fetch fails. The child class may want to reset components
	// like rate limiter.
	@MainThread
	protected fun onFetchFailed() {
	}
}

open class ResourceLiveData<ResultType, RequestType> : MediatorLiveData<Resource<ResultType>>() {

	private val resource = NetworkBoundResource(this)

	fun setupResource(resourceCallback: NetworkBoundResource.Callback<ResultType, RequestType>) {
		resource.setup(resourceCallback)
	}
}