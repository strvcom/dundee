package com.strv.dundee.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.strv.dundee.api.bitstamp.BitstampApi
import com.strv.dundee.api.bitstamp.TickerResponse
import com.strv.ktools.inject
import com.strv.ktools.logMe
import com.strv.ktools.then

class MainViewModel() : ViewModel() {
    val bitstampApi by inject<BitstampApi>()
    val ticker = ObservableField<TickerResponse>()

    init {
        bitstampApi.getPrice(BitstampApi.Coin.BTC, BitstampApi.Currency.USD).then { response, error ->
            response.logMe()
            response?.let { ticker.set(response.body()) }
            error?.printStackTrace()
        }
    }
}