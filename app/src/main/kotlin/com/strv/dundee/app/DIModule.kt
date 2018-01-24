package com.strv.dundee.app

import android.app.Application
import android.arch.persistence.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.api.exchangerate.ExchangeRateApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.cache.ExchangeRatesCache
import com.strv.dundee.model.db.BitcoinDatabase
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.ExchangeRatesRepository
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.provideSingleton

object DIModule {
	fun initialize(application: Application) {
		provideSingleton { application }
		provideSingleton { Config }

		provideSingleton { BitstampApi() }
		provideSingleton { BitfinexApi() }
		provideSingleton { ExchangeRateApi() }

		provideSingleton { BitcoinCache() }
		provideSingleton { ExchangeRatesCache() }

		provideSingleton { BitcoinRepository() }
		provideSingleton { ExchangeRatesRepository() }


		val database = Room.databaseBuilder(application, BitcoinDatabase::class.java, "bitcoin-database").build()
		provideSingleton { database.tickerDao() }
		provideSingleton { database.exchangeRatesDao() }


		provideSingleton { FirebaseFirestore.getInstance() }

		provideSingleton { FirebaseAuth.getInstance() }

		provideSingleton { UserRepository() }
		provideSingleton { WalletRepository() }

	}
}


