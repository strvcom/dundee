package com.strv.dundee.model.api.coincap

import android.app.Application
import android.arch.lifecycle.LiveData
import com.google.gson.Gson
import com.strv.dundee.app.Config
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.TimeFrame
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofit
import com.strv.ktools.inject
import com.strv.ktools.mapResource

class CoincapApi {
	val application by inject<Application>()
	val config by inject<Config>()
	val gson by inject<Gson>()

	val URL = "http://coincap.io/"

	val api = getRetrofit(application, URL, config.HTTP_LOGGING_LEVEL, gson = gson).create(CoincapApiInterface::class.java)

	fun getHistory(coin: String, timeFrame: TimeFrame): LiveData<Resource<History>> {
		return if (timeFrame == TimeFrame.ALL) api.getHistory(coin).mapResource({ it?.getHistory(coin, timeFrame) })
		else api.getHistory(coin, timeFrame.key).mapResource({ it?.getHistory(coin, timeFrame) })
	}
}