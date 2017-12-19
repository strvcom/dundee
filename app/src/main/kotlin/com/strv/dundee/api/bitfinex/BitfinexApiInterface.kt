package com.strv.dundee.api.bitfinex

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BitfinexApiInterface {

    @GET("ticker/t{currencyPair}")
    fun getTicker(@Path("currencyPair") currencyPair: String): Call<BitfinexTickerResponse>
}

