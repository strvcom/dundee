package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.inject
import com.strv.ktools.then


class BitcoinRepository {
    val cache by inject<BitcoinCache>()

    val bitstampApi by inject<BitstampApi>()
    val bitfinexApi by inject<BitfinexApi>()

    fun getTicker(source: String, coin: String, currency: String): LiveData<Ticker> {
        // in all cases, fetch new data
        val data = MutableLiveData<Ticker>()

        // put LiveData into cache - cache will observe live data for changes and store the latest value
        cache.putTicker(data)

        // pick the right api
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

        // try cache
        val cached = cache.getTicker(source, currency, coin)
        return cached ?: data
    }
}