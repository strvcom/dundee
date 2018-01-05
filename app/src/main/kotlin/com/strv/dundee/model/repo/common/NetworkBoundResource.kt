package com.strv.dundee.model.repo.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.strv.ktools.doAsync
import com.strv.ktools.uiThread
import retrofit2.Response

// ResultType: Type for the Resource data
// RequestType: Type for the API response
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread
internal constructor(liveDataToReuse: LiveData<Resource<ResultType>>? = null) {
	private val result: MediatorLiveData<Resource<ResultType>>

	// Called to save the result of the API response into the database
	@WorkerThread
	protected abstract fun saveCallResult(item: RequestType)

	// Called with the data in the database to decide whether it should be
	// fetched from the network.
	@MainThread
	protected abstract fun shouldFetch(data: ResultType?): Boolean

	// Called to get the cached data from the database
	@MainThread
	protected abstract fun loadFromDb(): LiveData<ResultType>

	// Called to create the API call.
	@MainThread
	protected abstract fun createCall(): LiveData<Response<out RequestType>>

	// Called when the fetch fails. The child class may want to reset components
	// like rate limiter.
	@MainThread
	protected fun onFetchFailed() {
	}

	init {
		if (liveDataToReuse == null) {
			result = MediatorLiveData()
			result.value = Resource(Status.LOADING, null)
		} else if (liveDataToReuse is MediatorLiveData<Resource<ResultType>>) {
			result = liveDataToReuse
			result.value = Resource(result.value?.status ?: Status.LOADING, result.value?.data, result.value?.message)
		} else {
			throw IllegalArgumentException("LiveData provided for reuse must be result of previous NetworkBoundResource instance.")
		}


		val dbSource = loadFromDb()
		result.addSource(dbSource, { data ->
			result.removeSource(dbSource)
			if (shouldFetch(data)) {
				fetchFromNetwork(dbSource)
			} else {
				result.addSource(dbSource, { newData -> result.setValue(Resource(Status.SUCCESS, newData)) })
			}
		})
	}

	private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
		val apiResponse = createCall()
		// we re-attach dbSource as a new source,
		// it will dispatch its latest value quickly
		result.addSource(dbSource, { newData -> result.setValue(Resource(Status.LOADING, newData)) })
		result.addSource(apiResponse, { response ->
			result.removeSource(apiResponse)
			result.removeSource(dbSource)

			if (response != null && response.isSuccessful) {
				saveResultAndReInit(response)
			} else {
				onFetchFailed()
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Status.ERROR, newData, response?.message()))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(response: Response<out RequestType>) {
		doAsync {
			saveCallResult(response.body()!!)
			uiThread {
				result.addSource(loadFromDb(), { newData -> result.setValue(Resource(Status.SUCCESS, newData)) })
			}
		}
	}


	fun getAsLiveData(): LiveData<Resource<ResultType>> = result
}
