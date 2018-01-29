package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.ArrayAdapter
import com.strv.dundee.R
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.ui.nav.MainNavigation
import com.strv.ktools.inject
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData


class MainViewModel() : ViewModel() {

	private val application by inject<Application>()
	private val userRepository by inject<UserRepository>()

	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val apiCurrency by application.sharedPrefs().stringLiveData(Currency.USD)
	val sourceAdapter = ArrayAdapter(application, R.layout.item_spinner_source_currency, BitcoinSource.getAll())
	val currencyAdapter = ArrayAdapter(application, R.layout.item_spinner_source_currency, Currency.getAll())
	val apiCurrencyAdapter = ArrayAdapter(application, R.layout.item_spinner_source_currency, Currency.getApiCurrencies())
	val optionsOpen = MutableLiveData<Boolean>().apply { value = false }
	val navigationManager = MainNavigation()

	fun logout() {
		userRepository.signOut()
	}
}