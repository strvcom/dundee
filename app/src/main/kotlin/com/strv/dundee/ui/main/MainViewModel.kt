package com.strv.dundee.ui.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.google.firebase.auth.FirebaseAuth
import com.strv.dundee.common.Firestore
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.common.Resource
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.inject
import com.strv.ktools.observe




class MainViewModel() : ViewModel(), LifecycleReceiver {
	val bitcoinRepository by inject<BitcoinRepository>()
	val ticker = ObservableField<Resource<Ticker>>()
	val currency = ObservableField<String>(Currency.USD)
	val coin = ObservableField<String>(Coin.BTC)

	init {
		Firestore.set(collection = "users", data = Firestore.getUser())
	}

	override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
		bitcoinRepository.getTicker(BitcoinSource.BITSTAMP, coin.get()!!, currency.get()!!).observe(lifecycleOwner, ticker)
	}


	fun logout() {
		FirebaseAuth.getInstance().signOut()
	}

}