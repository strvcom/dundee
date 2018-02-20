package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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

// map resource data
fun <T, S> LiveData<Resource<T>>.mapResource(mapFunction: (T?) -> S?) = this.map { it.map(mapFunction) }

// get live data from Retrofit call
fun <T> Call<T>.liveData(cancelOnInactive: Boolean = false) = RetrofitCallLiveData(this, cancelOnInactive)

// Retrofit CallAdapter Factory - use with Retrofit builder
class LiveDataCallAdapterFactory : CallAdapter.Factory() {
	override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
		if (CallAdapter.Factory.getRawType(returnType) != LiveData::class.java) {
			return null
		}
		if (returnType !is ParameterizedType) {
			throw IllegalStateException("Response must be parametrized as " + "LiveData<Resource<T>> or LiveData<? extends Resource>")
		}
		val responseType = CallAdapter.Factory.getParameterUpperBound(0, CallAdapter.Factory.getParameterUpperBound(0, returnType) as ParameterizedType)
		return LiveDataBodyCallAdapter<Any>(responseType)
	}
}

// get basic Retrofit setupCached with logger
internal fun <T> getRetrofitInterface(context: Context, url: String, apiInterface: Class<T>, logLevel: HttpLoggingInterceptor.Level, clientBuilderBase: OkHttpClient.Builder? = null): T {
	val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = logLevel
	}

	val client = (clientBuilderBase ?: OkHttpClient.Builder()).addInterceptor(loggingInterceptor).addInterceptor(ConnectivityInterceptor(context)).build()

	val gson = GsonBuilder().create()

	return Retrofit.Builder()
		.client(client)
		.baseUrl(url)
		.addCallAdapterFactory(LiveDataCallAdapterFactory())
		.addConverterFactory(GsonConverterFactory.create(gson))
		.build()
		.create(apiInterface)
}

// -- internal --

private class LiveDataBodyCallAdapter<R> internal constructor(private val responseType: Type) : CallAdapter<R, LiveData<Resource<R>>> {
	override fun responseType() = responseType
	override fun adapt(call: Call<R>) = call.liveData()
}

open class RetrofitCallLiveData<T>(val call: Call<T>, val cancelOnInactive: Boolean = false) : LiveData<Resource<T>>() {
	override fun onActive() {
		super.onActive()
		if (call.isExecuted)
			return
		call.then { response, error ->
			postValue(Resource.fromResponse(response, error))
		}
	}

	override fun onInactive() {
		super.onInactive()
		if (cancelOnInactive)
			call.cancel()
	}
}

class ConnectivityInterceptor(val context: Context) : Interceptor {
	override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
		if (!isOnline(context)) throw NoConnectivityException()
		val builder = chain!!.request().newBuilder()
		return chain.proceed(builder.build())
	}
}

class NoConnectivityException() : IOException("No connectivity exception")

fun isOnline(context: Context): Boolean {
	val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	return connectivityManager.activeNetworkInfo?.isConnected ?: false
}