package com.strv.dundee.base

import com.google.gson.Gson
import com.strv.ktools.provideSingleton

object TestDIModule {
	fun initialize() {
		provideSingleton { Gson() }
	}
}


