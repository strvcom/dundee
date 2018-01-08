package com.strv.dundee.ui.main

import android.R
import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.ArrayAdapter
import com.strv.dundee.firestore.Firestore
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.EventLiveData
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.logD
import com.strv.ktools.logMeD
import com.strv.ktools.publish


class AddAmountViewModel() : ViewModel(), LifecycleReceiver {

	val application by inject<Application>()
	val finish = EventLiveData<Unit>()
	val amount = MutableLiveData<String>()
	val amountValid = MediatorLiveData<Boolean>().addValueSource(amount, { it != null && it.isNotEmpty() && it.isNotBlank() })
	val progress = MutableLiveData<Boolean>().apply { value = false }
	var coin = Coin.BTC
	val coinAdapter = ArrayAdapter(application, R.layout.simple_spinner_dropdown_item, Coin.getAll())

	fun addAmount() {
		logD("Adding ${amount.value} $coin")
		progress.value = true

		val data = Wallet(coin = coin, amount = amount.value?.toDouble())
		val documentRef = Firestore.db.collection(Wallet.COLLECTION)
		documentRef
				.add(data)
				.addOnSuccessListener {
					logD("Added ${amount.value} $coin")
					finish.publish()
				}
				.addOnFailureListener { e ->
					e.logMeD()
					progress.value = false
				}
	}
}
