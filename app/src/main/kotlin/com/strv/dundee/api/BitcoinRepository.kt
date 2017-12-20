package com.strv.dundee.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.strv.dundee.api.bitfinex.BitfinexApi
import com.strv.dundee.api.bitstamp.BitstampApi
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker
import com.strv.ktools.inject
import com.strv.ktools.then


class BitcoinRepository {
    val cache by inject<BitcoinCache>()

    val bitstampApi by inject<BitstampApi>()
    val bitfinexApi by inject<BitfinexApi>()

    fun getTicker(source: BitcoinSource, coin: Coin, currency: Currency): LiveData<Ticker> {
        val data = MutableLiveData<Ticker>()

        // try cache
        val cached = cache.getTicker(source, currency, coin)
        if (cached != null) {
            data.value = cached.value
        } else {
            cache.putTicker(data)
        }

        val api = when (source){
            BitcoinSource.BITSTAMP -> bitstampApi
            BitcoinSource.BITFINEX -> bitfinexApi
        }


        api.getTicker(coin, currency).then { response, error ->
            error?.printStackTrace()
            response?.let {
                data.value = response.body()?.getTicker(source, currency, coin)
            }
        }

        return data
    }
}