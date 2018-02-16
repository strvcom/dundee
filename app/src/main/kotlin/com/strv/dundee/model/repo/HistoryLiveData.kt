package com.strv.dundee.model.repo

import com.strv.dundee.model.api.coincap.CoincapApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.History
import com.strv.dundee.model.entity.TimeFrame
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.inject

class HistoryLiveData : ResourceLiveData<History>() {
	val cache by inject<BitcoinCache>()
	val coincapApi by inject<CoincapApi>()

	fun refresh(coin: String, timeFrame: TimeFrame) {
		setupCached(object : NetworkBoundResource.Callback<History> {
			override fun saveCallResult(item: History) {
				cache.putHistory(item)
			}

			override fun shouldFetch(dataFromCache: History?) = true

			override fun loadFromDb() = cache.getHistory(coin, timeFrame)

			override fun createNetworkCall() = coincapApi.getHistory(coin, timeFrame)
		})
	}
}