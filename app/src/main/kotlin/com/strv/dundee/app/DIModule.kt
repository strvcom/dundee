package com.strv.dundee.app

import android.app.Application
import android.arch.persistence.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.db.BitcoinDatabase
import com.strv.dundee.model.repo.BitcoinCache
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.provideSingleton

object DIModule {
	fun initialize(application: Application) {
		provideSingleton { application }
		provideSingleton { Config }

		provideSingleton { BitstampApi() }
		provideSingleton { BitfinexApi() }

		provideSingleton { BitcoinCache() }
		provideSingleton { BitcoinRepository() }


		val database = Room.databaseBuilder(application, BitcoinDatabase::class.java, "bitcoin-database").build()
		provideSingleton { database.tickerDao() }


		provideSingleton { FirebaseFirestore.getInstance() }

		provideSingleton { FirebaseAuth.getInstance() }

		provideSingleton { UserRepository() }
		provideSingleton { WalletRepository() }
	}
}


