package com.strv.dundee.app

import android.app.Application
import android.arch.persistence.room.Room
import com.strv.dundee.repo.BitcoinCache
import com.strv.dundee.repo.BitcoinRepository
import com.strv.dundee.api.bitfinex.BitfinexApi
import com.strv.dundee.api.bitstamp.BitstampApi
import com.strv.dundee.db.BitcoinDatabase
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
        provideSingleton { database.userDao() }

    }
}


