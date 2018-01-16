package com.strv.dundee.model.entity

data class WalletOverview(
		var coin: String,
		var amount: Double = 0.0,
		var boughtPrice: Double = 0.0
)
