package com.strv.dundee.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.strv.dundee.api.BitcoinApi
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker
import com.strv.ktools.inject
import com.strv.ktools.logMe
import com.strv.ktools.observe
import com.strv.ktools.then

class MainViewModel() : ViewModel() {
    val bitcoinApi by inject<BitcoinApi>()
    val ticker = ObservableField<Ticker>()
    val currency = ObservableField<Currency>(Currency.USD)
    val coin = ObservableField<Coin>(Coin.BTC)

    init {
        fetchTicker()
        currency.observe { fetchTicker() }
        coin.observe { fetchTicker() }
    }

    private fun fetchTicker() {
        bitcoinApi.getTicker(coin.get()!!, currency.get()!!).then { response, error ->
            response.logMe()
            response?.let { ticker.set(response.body()?.getTicker()) }
            error?.printStackTrace()
        }
    }
}