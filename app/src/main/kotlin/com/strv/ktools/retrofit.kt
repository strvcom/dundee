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

fun <T> Call<T>.liveData(): LiveData<T> =
		object : LiveData<T>(), Callback<T> {
			override fun onResponse(call: Call<T>?, response: Response<T>) {
				value = response.body()
			}

			override fun onFailure(call: Call<T>?, t: Throwable) {
				t.printStackTrace()
			}

			override fun onActive() {
				this@liveData.enqueue(this)
			}

			override fun onInactive() {
				this@liveData.cancel()
			}
		}

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