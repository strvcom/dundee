package com.strv.dundee.model.api.coincap

import android.app.Application
import android.arch.lifecycle.LiveData
import com.strv.dundee.app.Config
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.TimeFrame
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.inject
import com.strv.ktools.mapResource

class CoincapApi {
	val application by inject<Application>()
	val config by inject<Config>()

	val URL = "http://coincap.io/"

	val api = getRetrofitInterface(application, URL, CoincapApiInterface::class.java, config.HTTP_LOGGING_LEVEL)

	fun getHistory(coin: String, timeFrame: TimeFrame): LiveData<Resource<History>> {
		return if (timeFrame == TimeFrame.ALL) api.getHistory(coin).mapResource({ it?.getHistory(coin, timeFrame) })
		else api.getHistory(coin, timeFrame.key).mapResource({ it?.getHistory(coin, timeFrame) })
	}
}