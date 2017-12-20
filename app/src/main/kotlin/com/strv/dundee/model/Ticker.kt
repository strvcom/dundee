package com.strv.dundee.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "user")
data class Ticker(
        @PrimaryKey @Embedded val source: BitcoinSource,
        @Embedded val currency: Currency,
        @Embedded val coin: Coin,
        val lastPrice: Double,
        val highPrice: Double,
        val lowPrice: Double,
        val timestamp: Long
)