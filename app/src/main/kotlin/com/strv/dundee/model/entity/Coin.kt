package com.strv.dundee.model.entity

object Coin {
	const val BTC = "BTC"
	const val XRP = "XRP"
	const val LTC = "LTC"
	const val ETH = "ETH"
	const val BCH = "BCH"

    fun getAll() = arrayOf(Coin.BTC, Coin.ETH, Coin.BCH, Coin.XRP, Coin.LTC)
}