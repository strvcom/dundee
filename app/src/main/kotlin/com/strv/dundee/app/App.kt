package com.strv.dundee.app

import android.app.Application
import com.strv.ktools.inject
import com.strv.ktools.setLogTag

class App : Application() {

	private val config by inject<Config>()

	init {
		DIModule.initialize(this)
		setLogTag(config.LOG_TAG)
	}
}