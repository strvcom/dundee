package com.strv.dundee.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.strv.dundee.model.Ticker

@Database(entities = arrayOf(Ticker::class), version = 1)
abstract class BitcoinDatabase : RoomDatabase(){
    abstract fun userDao(): TickerDao
}
