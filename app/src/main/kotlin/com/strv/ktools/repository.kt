package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import retrofit2.Response

class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {
	enum class Status { SUCCESS, ERROR, LOADING }
}

// ResultType: Type for the Resource data
// RequestType: Type for the API response
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread
internal constructor(liveDataToReuse: LiveData<Resource<ResultType>>? = null) {
	private val result: CleanableMediatorLiveData<Resource<ResultType>>

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
			result = CleanableMediatorLiveData()
			result.value = Resource(Resource.Status.LOADING, null)
		} else if (liveDataToReuse is CleanableMediatorLiveData<Resource<ResultType>>) {
			result = liveDataToReuse
			result.clearSources()
			result.value = Resource(result.value?.status ?: Resource.Status.LOADING, result.value?.data, result.value?.message)
		} else {
			throw IllegalArgumentException("LiveData provided for reuse must be result of previous NetworkBoundResource instance.")
		}


		val dbSource = loadFromDb()
		result.addSource(dbSource, { data ->
			result.removeSource(dbSource)
			if (shouldFetch(data)) {
				fetchFromNetwork(dbSource)
			} else {
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.SUCCESS, newData)) })
			}
		})
	}

	private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
		val apiResponse = createCall()
		// we re-attach dbSource as a new source,
		// it will dispatch its latest value quickly
		result.addSource(dbSource, { newData -> result.setValue(Resource(Resource.Status.LOADING, newData)) })
		result.addSource(apiResponse, { response ->
			result.removeSource(apiResponse)
			result.removeSource(dbSource)

			if (response != null && response.isSuccessful) {
				saveResultAndReInit(response)
			} else {
				onFetchFailed()
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.ERROR, newData, response?.message()))
				})
			}
		})
	}

	@MainThread
	private fun saveResultAndReInit(response: Response<out RequestType>) {
		doAsync {
			saveCallResult(response.body()!!)
			uiThread {
				val dbSource = loadFromDb()
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.SUCCESS, newData)) })
			}
		}
	}


	fun getAsLiveData(): LiveData<Resource<ResultType>> = result
}

class CleanableMediatorLiveData<T> : MediatorLiveData<T>(){

	private val sources = ArrayList<LiveData<*>>()

	override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<S>) {
		super.addSource(source, onChanged)
		sources.add(source)
	}

	override fun <S : Any?> removeSource(toRemote: LiveData<S>) {
		super.removeSource(toRemote)
		sources.remove(toRemote)
	}

	fun clearSources() {
		sources.forEach({super.removeSource(it)})
		sources.clear()
	}
}