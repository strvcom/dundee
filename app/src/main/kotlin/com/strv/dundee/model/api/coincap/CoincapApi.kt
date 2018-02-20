package com.strv.dundee.model.api.coincap

import android.app.Application
import android.arch.lifecycle.LiveData
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.TimeFrame
import com.strv.ktools.Resource
import com.strv.ktools.getRetrofitInterface
import com.strv.ktools.inject
import com.strv.ktools.mapLiveData

class CoincapApi {
	val application by inject<Application>()

	val URL = "http://coincap.io/"

	val api = getRetrofitInterface(application, URL, CoincapApiInterface::class.java)

	fun getHistory(coin: String, timeFrame: TimeFrame): LiveData<Resource<History>> {
		return if(timeFrame == TimeFrame.ALL) api.getHistory(coin).mapLiveData({ it?.getHistory(coin, timeFrame) })
		else api.getHistory(coin, timeFrame.key).mapLiveData({ it?.getHistory(coin, timeFrame) })
	}
}