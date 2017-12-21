package com.strv.dundee.model.repo

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.db.TickerDao
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.doAsync
import com.strv.ktools.inject
import com.strv.ktools.log


class BitcoinCache {

    val tickerDao by inject<TickerDao>()

    fun getTicker(source: String, currency: String, coin: String): LiveData<Ticker>? {
        val fromDb = tickerDao.getTicker(source, currency, coin)
        log("Reading ticker from db: ${fromDb.value}")
        return fromDb
    }

    fun putTicker(ticker: LiveData<Ticker>) {
        ticker.observeForever {
            it?.let {
                log("Saving ticker to db: $it")
                doAsync {
                    tickerDao.putTicker(it)
                }
            }
        }
    }
}