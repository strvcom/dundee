package com.strv.dundee.app

import android.app.Application
import com.strv.dundee.api.bitstamp.BitstampApi
import com.strv.ktools.provideSingleton

object DIModule {
    fun initialize(application: Application) {
        provideSingleton { application }
        provideSingleton { Config }

        provideSingleton { BitstampApi() }
    }
}


