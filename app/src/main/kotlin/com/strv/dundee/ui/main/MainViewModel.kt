package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.TimeFrame
import com.strv.dundee.model.repo.ExchangeRatesRepository
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.ui.nav.MainNavigation
import com.strv.ktools.inject
import com.strv.ktools.sharedPrefs
import com.strv.ktools.string
import com.strv.ktools.stringLiveData

class MainViewModel() : ViewModel() {

	private val application by inject<Application>()
	private val userRepository by inject<UserRepository>()
	private val exchangeRatesRepository by inject<ExchangeRatesRepository>()

	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val apiCurrency by application.sharedPrefs().stringLiveData(Currency.USD)
	var timeFrame by application.sharedPrefs().string(TimeFrame.ALL.key)
	val timeFrameEnum = MutableLiveData<TimeFrame>().apply { value = TimeFrame.fromString(timeFrame) }
	val optionsOpen = MutableLiveData<Boolean>().apply { value = false }
	val navigationManager = MainNavigation()
	val exchangeRates = exchangeRatesRepository.getExchangeRates(Currency.USD, Currency.getAll().toList())
	private val timeFrameObserver = Observer<TimeFrame> { timeFrame = it?.key }

	init {
		timeFrameEnum.observeForever(timeFrameObserver)
	}

	override fun onCleared() {
		super.onCleared()
		timeFrameEnum.removeObserver(timeFrameObserver)
	}

	fun logout() {
		userRepository.signOut()
	}
}