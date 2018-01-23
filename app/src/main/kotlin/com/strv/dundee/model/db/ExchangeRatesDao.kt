package com.strv.dundee.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.entity.ExchangeRates

@Dao
interface ExchangeRatesDao {

	@Insert(onConflict = REPLACE)
	fun putRates(rate: ExchangeRates)

	@Query("SELECT * FROM exchangeRates WHERE source = :arg0 LIMIT 1")
	fun getRates(source: String): LiveData<ExchangeRates>
}