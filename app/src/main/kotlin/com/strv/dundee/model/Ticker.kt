package com.strv.dundee.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "ticker")
data class Ticker(

        var source: String = BitcoinSource.BITSTAMP,
        var currency: String = Currency.USD,
        var coin: String = Coin.BTC,
        var lastPrice: Double = 0.toDouble(),
        var highPrice: Double = 0.toDouble(),
        var lowPrice: Double = 0.toDouble(),
        var timestamp: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}