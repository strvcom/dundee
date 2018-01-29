package com.strv.dundee.model.entity

object BitcoinSource {
	const val BITSTAMP = "BITSTAMP"
	const val BITFINEX = "BITFINEX"

	fun getAll() = arrayOf(BitcoinSource.BITSTAMP, BitcoinSource.BITFINEX)
}