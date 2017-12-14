package com.strv.dundee.api.bitstamp

data class TickerResponse(
        val last: String,
        val high: String,
        val low: String,
        val timestamp: String,
        val bid: String,
        val vwap: String,
        val volume: String,
        val ask: String,
        val open: String
)