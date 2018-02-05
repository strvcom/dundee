package com.strv.dundee.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.Ticker

@Database(entities = arrayOf(Ticker::class, ExchangeRates::class, History::class), version = 1)
@TypeConverters(Converters::class)
abstract class BitcoinDatabase : RoomDatabase() {
	abstract fun tickerDao(): TickerDao
	abstract fun exchangeRatesDao(): ExchangeRatesDao
	abstract fun historyDao(): HistoryDao
}
