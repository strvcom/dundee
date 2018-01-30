package com.strv.dundee.app

import android.app.Application
import com.strv.dundee.BuildConfig
import com.strv.ktools.inject
import com.strv.ktools.setLogEnabled
import com.strv.ktools.setLogTag

class App : Application() {

	private val config by inject<Config>()

	override fun onCreate() {
		super.onCreate()

		DIModule.initialize(this)
		setLogTag(config.LOG_TAG)
		setLogEnabled(BuildConfig.LOGS)
	}
}