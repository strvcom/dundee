package com.strv.dundee.model.cache

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.db.ExchangeRateDao
import com.strv.dundee.model.entity.ExchangeRate
import com.strv.ktools.inject
import com.strv.ktools.log


class ExchangeRateCache {
	private val exchangeRateDao by inject<ExchangeRateDao>()

	fun getRate(source: String, target: String): LiveData<ExchangeRate> {
		val fromDb = exchangeRateDao.getRate(source, target)
		log("Reading exchange rate from db: ${fromDb.value}")
		return fromDb
	}

	fun putRate(rate: ExchangeRate) {
		log("Saving exchange rate to db: $rate")
		exchangeRateDao.putRate(rate)
	}
}