package com.strv.dundee.model.repo.common

import android.arch.lifecycle.LiveData
import com.strv.ktools.then
import retrofit2.Call
import retrofit2.Response

class RetrofitCallLiveData<T>(val call: Call<out T>, val cancelOnInactive: Boolean = false) : LiveData<Response<out T>>() {
	override fun onActive() {
		super.onActive()
		if (call.isExecuted)
			return
		call.then { response, error ->
			value = response
		}
	}

	override fun onInactive() {
		super.onInactive()
		if (cancelOnInactive)
			call.cancel()
	}

}