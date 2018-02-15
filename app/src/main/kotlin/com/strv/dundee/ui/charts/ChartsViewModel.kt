package com.strv.dundee.ui.charts

import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.HistoryLiveData
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.inject

class ChartsViewModel(val mainViewModel: MainViewModel) : ViewModel() {
	private val bitcoinRepository by inject<BitcoinRepository>()
	val history = HashMap<String, HistoryLiveData>()

	init {
		Coin.getAll().forEach { history[it] = bitcoinRepository.getHistory(it,mainViewModel.timeFrameEnum.value!!) }
		history.forEach { (coin, history) ->
			history.addSource(mainViewModel.timeFrameEnum, { history.refresh(coin, mainViewModel.timeFrameEnum.value!!) })
		}
	}
}