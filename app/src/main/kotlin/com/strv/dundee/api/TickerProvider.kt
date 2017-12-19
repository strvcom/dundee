package com.strv.dundee.api

import com.strv.dundee.model.Ticker


interface TickerProvider {
    fun getTicker(): Ticker
}