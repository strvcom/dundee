package com.strv.dundee.ui.main

import android.R
import android.app.Application
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.widget.ArrayAdapter
import com.strv.dundee.firestore.Firestore
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.SingleLiveData
import com.strv.ktools.inject
import com.strv.ktools.logD
import com.strv.ktools.logMeD


class AddAmountViewModel() : ViewModel(), LifecycleReceiver {

	val application by inject<Application>()
	val finish = SingleLiveData<Boolean>()
	val amount = ObservableField<String>()
	val amountValid = ObservableBoolean(false)
	val progress = ObservableBoolean(false)
	var coin = Coin.BTC
	val coinAdapter = ArrayAdapter(application, R.layout.simple_spinner_dropdown_item, Coin.getAll())

	fun checkInput() {
		amountValid.set(!(amount.get() == null || amount.get()!!.isEmpty()))
	}

	fun addAmount() {
		logD("Adding ${amount.get()} $coin")
		progress.set(true)

		val data = Wallet(coin = coin, amount = amount.get()?.toDouble())
		val documentRef = Firestore.db.collection("wallets")
		documentRef
				.add(data)
				.addOnSuccessListener {
					logD("Added ${amount.get()} $coin")
					finish.value = true
				}
				.addOnFailureListener { e ->
					e.logMeD()
					progress.set(false)
				}
	}
}