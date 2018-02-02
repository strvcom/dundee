package com.strv.dundee.model.entity

object BitcoinSource {
	const val BITSTAMP = "BITSTAMP"
	const val BITFINEX = "BITFINEX"
	const val COINCAP = "COINCAP"

	fun getAll() = arrayOf(BitcoinSource.BITSTAMP, BitcoinSource.BITFINEX, BitcoinSource.COINCAP)
}