package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import com.strv.dundee.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

// shorthand for enqueue call
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

// map response to another response using body map function
fun <T, S> Response<T>.map(mapFunction: (T?) -> S?) = if (isSuccessful) Response.success(mapFunction(body()), raw()) else Response.error(errorBody(), raw())

// get live data from Retrofit call
fun <T> Call<T>.liveData(cancelOnInactive: Boolean = false) = RetrofitCallLiveData(this, cancelOnInactive)

// get live data from Retrofit call and map response body to another object
fun <T, S> Call<T>.mapLiveData(mapFunction: (T?) -> S?, cancelOnInactive: Boolean = false) = RetrofitMapCallLiveData(this, mapFunction, cancelOnInactive)

// get basic Retrofit setupCached with logger
internal fun <T> getRetrofitInterface(context: Context, url: String, apiInterface: Class<T>, clientBuilderBase: OkHttpClient.Builder? = null): T {
	val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
	}

	val client = (clientBuilderBase ?: OkHttpClient.Builder()).addInterceptor(loggingInterceptor).addInterceptor(ConnectivityInterceptor(context)).build()

	val gson = GsonBuilder().create()

	return Retrofit.Builder()
		.client(client)
		.baseUrl(url)
		.addConverterFactory(GsonConverterFactory.create(gson))
		.build()
		.create(apiInterface)
}

// -- internal --

open class RetrofitMapCallLiveData<T, S>(val call: Call<T>, val mapFunction: (T?) -> S?, val cancelOnInactive: Boolean = false) : LiveData<Resource<S>>() {
	override fun onActive() {
		super.onActive()
		if (call.isExecuted)
			return
		call.then { response, error ->
			val mappedResponse = response?.map(mapFunction)
			value = Resource.fromResponse(mappedResponse, error)
		}
	}

	override fun onInactive() {
		super.onInactive()
		if (cancelOnInactive)
			call.cancel()
	}
}

class RetrofitCallLiveData<T>(call: Call<T>, cancelOnInactive: Boolean = false) : RetrofitMapCallLiveData<T, T>(call, { it }, cancelOnInactive)

class ConnectivityInterceptor(val context: Context) : Interceptor {
	override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
		if (!isOnline(context)) throw NoConnectivityException()
		val builder = chain!!.request().newBuilder()
		return chain.proceed(builder.build())
	}
}

class NoConnectivityException() : IOException() {
	override val message: String?
		get() = "No connectivity exception"
}

fun isOnline(context: Context): Boolean {
	val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	return connectivityManager.activeNetworkInfo?.isConnected ?: false
}