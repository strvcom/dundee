package com.strv.dundee.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.strv.dundee.api.bitfinex.BitfinexApi
import com.strv.dundee.api.bitstamp.BitstampApi
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Ticker
import com.strv.ktools.inject
import com.strv.ktools.then


class BitcoinRepository {
    val cache by inject<BitcoinCache>()

    val bitstampApi by inject<BitstampApi>()
    val bitfinexApi by inject<BitfinexApi>()

    fun getTicker(source: String, coin: String, currency: String): LiveData<Ticker> {

        // try cache
        val cached = cache.getTicker(source, currency, coin)

        // in all cases, fetch new data
        val data = MutableLiveData<Ticker>()
        cache.putTicker(data)

        val api = when (source) {
            BitcoinSource.BITSTAMP -> bitstampApi
            BitcoinSource.BITFINEX -> bitfinexApi
            else -> bitstampApi
        }


        api.getTicker(coin, currency).then { response, error ->
            // TODO: handle error
            error?.printStackTrace()
            response?.let {
                // cache observes data so it will save it's value automatically
                data.value = response.body()?.getTicker(source, currency, coin)
            }
        }

        return cached ?: data
    }
}