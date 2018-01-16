package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.ArrayAdapter
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.EventLiveData
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.logD
import com.strv.ktools.logMeD
import com.strv.ktools.publish


class AddAmountViewModel(wallet: Wallet? = null) : ViewModel(), LifecycleReceiver {

	private val application by inject<Application>()
	private val walletRepository by inject<WalletRepository>()

	val finish = EventLiveData<Unit>()

	val amount = MutableLiveData<String>()
	val boughtFor = MutableLiveData<String>()
	val amountValid = MediatorLiveData<Boolean>()
			.addValueSource(amount, { it != null && it.isNotEmpty() && it.isNotBlank() })
	val progress = MutableLiveData<Boolean>().apply { value = false }
	var coin = MutableLiveData<String>().apply { value = Coin.BTC }
	val coinAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Coin.getAll())
	var currency = MutableLiveData<String>().apply { value = Currency.USD }
	val currencyAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Currency.getAll())
	var wallet: Wallet? = null

	init {
		wallet?.let {
			amount.value = it.amount.toString()
			coin.value = it.coin
			it.boughtPrice?.let { boughtFor.value = it.toString() }
			it.boughtCurrency?.let { currency.value = it }
			this.wallet = it
		}
	}

	fun saveAmount() {
		if (wallet != null) updateAmount()
		else addAmount()
	}

	private fun addAmount() {
		logD("Adding ${amount.value} $coin")
		progress.value = true

		val data = Wallet(coin = coin.value, amount = amount.value?.toDouble())
		if (boughtFor.value != null && !boughtFor.value!!.isEmpty()) {
			data.boughtPrice = boughtFor.value?.toDouble()
			data.boughtCurrency = currency.value
		}

		walletRepository.addWalletToCurrentUser(data)
				.addOnSuccessListener {
					logD("Added ${amount.value} $coin")
					finish.publish()
				}
				.addOnFailureListener { e ->
					e.logMeD()
					progress.value = false
				}
	}

	private fun updateAmount() {
		logD("Update to ${amount.value} $coin")
		progress.value = true

		wallet?.amount = amount.value?.toDouble()
		wallet?.coin = coin.value
		walletRepository.updateWallet(wallet!!)
				.addOnSuccessListener {
					logD("Updated to ${amount.value} $coin")
					finish.publish()
				}
				.addOnFailureListener { e ->
					e.logMeD()
					progress.value = false
				}
	}
}
