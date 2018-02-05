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
		Coin.getAll().forEach { history[it] = bitcoinRepository.getHistory(mainViewModel.source.value!!, it, mainViewModel.apiCurrency.value!!) }
		history.forEach { coin, history ->
			history.addSource(mainViewModel.apiCurrency, { history.refresh(mainViewModel.source.value!!, coin, mainViewModel.apiCurrency.value!!) })
			history.addSource(mainViewModel.source, { history.refresh(mainViewModel.source.value!!, coin, mainViewModel.apiCurrency.value!!) })
		}
	}
}