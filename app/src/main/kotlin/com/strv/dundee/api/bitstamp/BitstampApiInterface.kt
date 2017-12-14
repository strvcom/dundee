package com.strv.dundee.api.bitstamp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface BitstampApiInterface {

    @GET("ticker/{currencyPair}")
    fun getTicker(@Path("currencyPair") currencyPair: String): Call<TickerResponse>
}
