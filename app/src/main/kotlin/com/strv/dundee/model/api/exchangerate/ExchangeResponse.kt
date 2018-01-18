package com.strv.dundee.model.api.exchangerate

data class ExchangeResponse(
		val base: String,
		val date: String,
		val rates: HashMap<String, Double>
)