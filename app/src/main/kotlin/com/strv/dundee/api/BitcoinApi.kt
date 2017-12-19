package com.strv.dundee.api

import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import retrofit2.Call


interface BitcoinApi {
    fun getTicker(coin: Coin, currency: Currency): Call<out TickerProvider>
}