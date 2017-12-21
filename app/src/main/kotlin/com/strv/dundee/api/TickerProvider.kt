package com.strv.dundee.api

import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker


interface TickerProvider {
    fun getTicker(source: String, currency: String, coin: String): Ticker
}