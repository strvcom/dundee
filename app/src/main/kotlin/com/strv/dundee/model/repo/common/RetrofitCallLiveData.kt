package com.strv.dundee.model.repo.common

import android.arch.lifecycle.LiveData
import com.strv.ktools.then
import retrofit2.Call
import retrofit2.Response

class RetrofitCallLiveData<T>(val call: Call<out T>) : LiveData<Response<out T>>() {
    override fun onActive() {
        super.onActive()
        call.then { response, error ->
            value = response
        }
    }

}