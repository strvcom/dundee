package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.repo.ExchangeRatesRepository
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.ui.nav.MainNavigation
import com.strv.ktools.inject
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData

class MainViewModel() : ViewModel() {

	private val application by inject<Application>()
	private val userRepository by inject<UserRepository>()
	private val exchangeRatesRepository by inject<ExchangeRatesRepository>()

	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val apiCurrency by application.sharedPrefs().stringLiveData(Currency.USD)
	val optionsOpen = MutableLiveData<Boolean>().apply { value = false }
	val navigationManager = MainNavigation()
	val exchangeRates = exchangeRatesRepository.getExchangeRates(Currency.USD, Currency.getAll().toList())

	fun logout() {
		userRepository.signOut()
	}
}