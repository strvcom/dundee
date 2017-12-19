package com.strv.dundee.app

import android.app.Application
import com.strv.dundee.api.BitcoinApi
import com.strv.dundee.api.bitfinex.BitfinexApi
import com.strv.ktools.provideSingleton

object DIModule {
    fun initialize(application: Application) {
        provideSingleton { application }
        provideSingleton { Config }

        provideSingleton<BitcoinApi> { BitfinexApi() }
    }
}


