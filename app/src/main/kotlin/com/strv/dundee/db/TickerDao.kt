package com.strv.dundee.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker


@Dao
interface TickerDao {

    @Insert(onConflict = REPLACE)
    fun putTicker(ticker: Ticker)

    @Query("SELECT * FROM ticker WHERE source = :arg0 AND currency = :arg1 AND coin = :arg2 LIMIT 1")
    fun getTicker(source: BitcoinSource, currency: Currency, coin: Coin): LiveData<Ticker>
}