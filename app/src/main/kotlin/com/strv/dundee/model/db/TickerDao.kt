package com.strv.dundee.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.entity.Ticker


@Dao
interface TickerDao {

	@Insert(onConflict = REPLACE)
	fun putTicker(ticker: Ticker)

	@Query("SELECT * FROM ticker WHERE source = :arg0 AND currency = :arg1 AND coin = :arg2 LIMIT 1")
	fun getTicker(source: String, currency: String, coin: String): LiveData<Ticker>
}