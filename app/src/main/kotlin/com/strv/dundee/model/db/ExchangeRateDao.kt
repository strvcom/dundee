package com.strv.dundee.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.entity.ExchangeRate


@Dao
interface ExchangeRateDao {

	@Insert(onConflict = REPLACE)
	fun putRate(rate: ExchangeRate)

	@Query("SELECT * FROM exchangeRate WHERE source = :arg0 AND target = :arg1 LIMIT 1")
	fun getRate(source: String, target: String): LiveData<ExchangeRate>
}