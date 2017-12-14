package com.strv.dundee.app

import android.app.Application
import com.strv.ktools.setLogTag

class App : Application() {

    init {
        DIModule.initialize(this)
        setLogTag("dundee")
    }


}