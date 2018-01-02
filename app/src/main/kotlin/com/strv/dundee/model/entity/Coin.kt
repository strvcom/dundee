package com.strv.dundee.model.entity

object Coin {
	const val BTC = "BTC"
	const val XRP = "XRP"
	const val LTC = "LTC"
	const val ETH = "ETH"
	const val BCH = "BCH"

    fun getList(): List<String> {
        val result = ArrayList<String>()
        result.add(BTC)
        result.add(XRP)
        result.add(LTC)
        result.add(ETH)
        result.add(BCH)
        return result
    }
}