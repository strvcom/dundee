package com.strv.dundee.api

import android.arch.lifecycle.LiveData
import com.strv.dundee.db.TickerDao
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker
import com.strv.ktools.inject


class BitcoinCache {

    val tickerDao by inject<TickerDao>()

    fun getTicker(source: BitcoinSource, currency: Currency, coin: Coin): LiveData<Ticker>? {
        return tickerDao.getTicker(source, currency, coin)
    }

    fun putTicker(ticker: LiveData<Ticker>) {
        ticker.observeForever {
            it?.let {
                tickerDao.putTicker(it)
            }
        }
    }
}