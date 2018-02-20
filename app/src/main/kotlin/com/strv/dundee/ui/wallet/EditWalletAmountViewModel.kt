package com.strv.dundee.ui.wallet

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
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.logD
import com.strv.ktools.logMeD
import com.strv.ktools.mutableLiveDataOf
import com.strv.ktools.publish
import java.util.Date

class EditWalletAmountViewModel(wallet: Wallet? = null) : ViewModel() {

	private val application by inject<Application>()
	private val walletRepository by inject<WalletRepository>()

	val finish = EventLiveData<Unit>()

	val amount = MutableLiveData<String>()
	val boughtFor = MutableLiveData<String>()
	val boughtOn = mutableLiveDataOf(Date())
	private val validateFunction = {
		amount.value != null && amount.value!!.isNotEmpty() && amount.value!!.isNotBlank() &&
			boughtFor.value != null && boughtFor.value!!.isNotEmpty() && boughtFor.value!!.isNotBlank()
	}
	val amountValid = MediatorLiveData<Boolean>()
		.addValueSource(amount, { validateFunction() })
		.addValueSource(boughtFor, { validateFunction() })
	val progress = MutableLiveData<Boolean>().apply { value = false }
	var coin = MutableLiveData<String>().apply { value = Coin.BTC }
	var currency = MutableLiveData<String>().apply { value = Currency.USD }
	val coinAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Coin.getAll())
	val currencyAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Currency.getAll())
	var wallet: Wallet? = null

	init {
		wallet?.let {
			amount.value = it.amount.toString()
			coin.value = it.coin
			boughtFor.value = it.boughtPrice.toString()
			currency.value = it.boughtCurrency
			boughtOn.value = it.boughtDate
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

		val data = Wallet(coin = coin.value, amount = amount.value?.toDouble(), boughtPrice = boughtFor.value?.toDouble(), boughtCurrency = currency.value, boughtDate = boughtOn.value)
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

		wallet?.boughtPrice = boughtFor.value?.toDouble()
		wallet?.boughtCurrency = currency.value
		wallet?.amount = amount.value?.toDouble()
		wallet?.coin = coin.value
		wallet?.boughtDate = boughtOn.value
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
