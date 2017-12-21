package com.strv.dundee.api

import com.strv.dundee.api.TickerProvider
import retrofit2.Call


interface BitcoinApi{

    fun getTicker(coin: String, currency: String): Call<out TickerProvider>
}