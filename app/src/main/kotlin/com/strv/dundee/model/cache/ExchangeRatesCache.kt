package com.strv.dundee.model.cache

import android.arch.lifecycle.LiveData
import com.strv.dundee.model.db.ExchangeRatesDao
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.ktools.inject
import com.strv.ktools.log


class ExchangeRatesCache {
	private val exchangeRatesDao by inject<ExchangeRatesDao>()

	fun getRates(source: String): LiveData<ExchangeRates> {
		val fromDb = exchangeRatesDao.getRates(source)
		log("Reading exchange rates from db: ${fromDb.value}")
		return fromDb
	}

	fun putRates(rate: ExchangeRates) {
		log("Saving exchange rates to db: $rate")
		exchangeRatesDao.putRates(rate)
	}
}