package com.strv.dundee.ui.charts

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.CandleSet
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.Resource
import com.strv.ktools.inject

class ChartsViewModel(private val mainViewModel: MainViewModel) : ViewModel() {
	private val bitcoinRepository by inject<BitcoinRepository>()
	var candles: LiveData<Resource<CandleSet>>? = null
	val timeFrame = MutableLiveData<String>().apply { value = "1D" }

	init {
		refreshCandles()
		mainViewModel.apiCurrency.observeForever { refreshCandles() }

	}

	private fun refreshCandles() {
		candles = bitcoinRepository.getCandles(BitcoinSource.BITFINEX, Coin.BTC, mainViewModel.apiCurrency.value!!, timeFrame.value!!, liveDataToReuse = candles)
	}
}