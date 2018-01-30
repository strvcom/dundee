package com.strv.dundee.ui.charts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.inject

class ChartsViewModel(private val mainViewModel: MainViewModel) : ViewModel() {
	private val bitcoinRepository by inject<BitcoinRepository>()
	val timeFrame = MutableLiveData<String>().apply { value = "1D" }
	var candles = bitcoinRepository.getCandles(BitcoinSource.BITFINEX, Coin.BTC, mainViewModel.apiCurrency.value!!, timeFrame.value!!)

	init {
		mainViewModel.apiCurrency.observeForever { refreshCandles() }
	}

	private fun refreshCandles() {
		candles.refresh(BitcoinSource.BITFINEX, Coin.BTC, mainViewModel.apiCurrency.value!!, timeFrame.value!!)
	}
}