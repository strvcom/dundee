package com.strv.dundee.model.repo

class ExchangeRatesRepository {

	fun getExchangeRates(source: String, target: List<String>) = ExchangeRatesLiveData().apply { refresh(source, target) }
}