package com.strv.dundee.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import android.widget.AdapterView
import com.strv.dundee.firestore.Firestore
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.SingleLiveData
import com.strv.ktools.logD
import com.strv.ktools.logMeD


class AddAmountViewModel() : ViewModel(), LifecycleReceiver {

	val finish = SingleLiveData<Boolean>()
	val amount = ObservableField<String>()
	val amountValid = ObservableBoolean(false)
	val progress = ObservableBoolean(false)
	val listener = ObservableField<AdapterView.OnItemSelectedListener>()
	var selectedCoin = Coin.BTC


	init {
		listener.set(object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
				selectedCoin = Coin.getList().get(pos)
			}
		})
	}

	fun checkInput() {
		amountValid.set(!(amount.get() == null || amount.get()!!.isEmpty()))
	}

	fun addAmount() {
		logD("Adding ${amount.get()} $selectedCoin")
		progress.set(true)

		val data = Wallet(coin = selectedCoin, amount = amount.get()?.toDouble())
		val documentRef = Firestore.db.collection("wallets")
		documentRef
				.add(data)
				.addOnSuccessListener {
					logD("Added ${amount.get()} $selectedCoin")
					finish.value = true
				}
				.addOnFailureListener { e ->
					e.logMeD()
					progress.set(false)
				}
	}
}