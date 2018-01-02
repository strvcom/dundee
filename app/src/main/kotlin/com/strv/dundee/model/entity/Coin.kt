package com.strv.dundee.model.entity

object Coin {
    const val BTC = "btc"
    const val XRP = "xrp"
    const val LTC = "ltc"
    const val ETH = "eth"
    const val BCH = "bch"

    fun getList(): List<String> {
        val result = ArrayList<String>()
        result.add(BTC.toUpperCase())
        result.add(XRP.toUpperCase())
        result.add(LTC.toUpperCase())
        result.add(ETH.toUpperCase())
        result.add(BCH.toUpperCase())
        return result
    }
}