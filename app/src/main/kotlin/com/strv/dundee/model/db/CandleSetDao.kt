package com.strv.dundee.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.entity.CandleSet

@Dao
interface CandleSetDao {

	@Insert(onConflict = REPLACE)
	fun putCandleSet(ticker: CandleSet)

	@Query("SELECT * FROM candleSet WHERE source = :source AND currency = :currency AND coin = :coin AND timeFrame = :timeFrame LIMIT 1")
	fun getCandleSet(source: String, currency: String, coin: String, timeFrame: String): LiveData<CandleSet>
}