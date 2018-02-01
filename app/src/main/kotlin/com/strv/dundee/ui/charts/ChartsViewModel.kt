package com.strv.dundee.ui.charts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.CandlesLiveData
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.inject

class ChartsViewModel(val mainViewModel: MainViewModel) : ViewModel() {
	private val bitcoinRepository by inject<BitcoinRepository>()
	val timeFrame = MutableLiveData<String>().apply { value = "1D" }
	val candles = HashMap<String, CandlesLiveData>()

	init {
		Coin.getAll().forEach { candles[it] = bitcoinRepository.getCandles(BitcoinSource.BITFINEX, it, mainViewModel.apiCurrency.value!!, timeFrame.value!!) }
		candles.forEach { coin, candle ->
			candle.addSource(mainViewModel.apiCurrency, { candle.refresh(BitcoinSource.BITFINEX, coin, mainViewModel.apiCurrency.value!!, timeFrame.value!!) })
		}
	}
}