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
		fun createCall(): LiveData<Response<out RequestType>>
	}

	private var callback: Callback<ResultType, RequestType>? = null

	init {
		result.value = Resource(Resource.Status.LOADING, null)
	}

	fun setup(resourceCallback: Callback<ResultType, RequestType>) {
		callback = resourceCallback
		result.clearSources()
		result.value = Resource(result.value?.status
			?: Resource.Status.LOADING, result.value?.data, result.value?.message)

		val dbSource = callback!!.loadFromDb()
		result.addSource(dbSource, { data ->
			result.removeSource(dbSource)
			if (callback!!.shouldFetch(data)) {
				fetchFromNetwork(dbSource)
			} else {
				result.addSource(dbSource, { newData ->
					result.setValue(Resource(Resource.Status.SUCCESS, newData))
				})
			}
		})
	}

	private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
		checkCallback()
		val apiResponse = callback!!.createCall()
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
			checkCallback()
			callback!!.saveCallResult(response.body()!!)
			uiThread {
				checkCallback()
				val dbSource = callback!!.loadFromDb()
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

	private fun checkCallback() {
		if (callback == null) throw IllegalStateException("NetworkBoundResource Callback not defined")
	}

	fun getAsLiveData(): LiveData<Resource<ResultType>> = result
}

open class ResourceLiveData<ResultType, RequestType> : MediatorLiveData<Resource<ResultType>>() {

	private val resource = NetworkBoundResource<ResultType, RequestType>(this)

	private val sources = ArrayList<LiveData<*>>()

	fun setupResource(resourceCallback: NetworkBoundResource.Callback<ResultType, RequestType>) {
		resource.setup(resourceCallback)
	}

	override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<S>) {
		super.addSource(source, onChanged)
		sources.add(source)
	}

	override fun <S : Any?> removeSource(toRemote: LiveData<S>) {
		super.removeSource(toRemote)
		sources.remove(toRemote)
	}

	fun clearSources() {
		sources.forEach({ super.removeSource(it) })
		sources.clear()
	}
}