package com.strv.ktools

import android.arch.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.strv.dundee.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun <T> Call<T>.then(callback: (response: Response<T>?, error: Throwable?) -> Unit) {
	enqueue(object : Callback<T> {
		override fun onResponse(call: Call<T>, response: Response<T>) {
			callback(response, null)
		}

		override fun onFailure(call: Call<T>, t: Throwable) {
			callback(null, t)
		}
	})
}
fun <T, S> Response<T>.map(mapFunction: (T?) -> S?) = if (isSuccessful) Response.success(mapFunction(body()), raw()) else Response.error(errorBody(), raw())

fun <T> Call<T>.liveData(cancelOnInactive: Boolean = false) = RetrofitCallLiveData(this, cancelOnInactive)
fun <T, S> Call<T>.mapLiveData(mapFunction: (T?) -> S?, cancelOnInactive: Boolean = false) = RetrofitMapCallLiveData(this, mapFunction, cancelOnInactive)


internal fun <T> getRetrofitInterface(url: String, apiInterface: Class<T>, clientBuilderBase: OkHttpClient.Builder? = null): T {
	val interceptor = HttpLoggingInterceptor().apply {
		level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
	}

	val client = (clientBuilderBase ?: OkHttpClient.Builder()).addInterceptor(interceptor).build()

	val gson = GsonBuilder().create()

	return Retrofit.Builder()
		.client(client)
		.baseUrl(url)
		.addConverterFactory(GsonConverterFactory.create(gson))
		.build()
		.create(apiInterface)
}

open class RetrofitMapCallLiveData<T, S>(val call: Call<T>, val mapFunction: (T?) -> S?, val cancelOnInactive: Boolean = false) : LiveData<Response<S>>() {
	override fun onActive() {
		super.onActive()
		if (call.isExecuted)
			return
		call.then { response, error ->
			value = response?.map(mapFunction)
		}
	}

	override fun onInactive() {
		super.onInactive()
		if (cancelOnInactive)
			call.cancel()
	}
}

class RetrofitCallLiveData<T>(call: Call<T>, cancelOnInactive: Boolean = false) : RetrofitMapCallLiveData<T, T>(call, { it }, cancelOnInactive)