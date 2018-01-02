package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.common.Resource
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.inject
import com.strv.ktools.observableString
import com.strv.ktools.observe
import com.strv.ktools.sharedPrefs


class MainViewModel() : ViewModel(), LifecycleReceiver {
	val application by inject<Application>()
	var lifecycleOwner: LifecycleOwner? = null

	val bitcoinRepository by inject<BitcoinRepository>()
	var tickerData: LiveData<Resource<Ticker>>
	val ticker = ObservableField<Resource<Ticker>>()
	val source by application.sharedPrefs().observableString(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().observableString(Currency.USD)
	val coin by application.sharedPrefs().observableString(Coin.BTC)

	val coinAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Coin.getAll())
	val currencyAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Currency.getAll())
	val sourceAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, BitcoinSource.getAll())

	init {
//		Firestore.set("users", User("Leos Dostal", 1990, true, Date()))
//		Firestore.updateField("users", "0aklFr7oq3Sx", "age,", 19)
//		Firestore.getDocument("users", "zw7TXkMcb1fKuJunlB5h", User::class.java)
//		Firestore.getDocument("users", "zw7TXkMcb1fKuJunlB5h")
//		Firestore.getDocuments("users", Pair("name", "Cecil"))
//		Firestore.getDocuments("users", User::class.java)
//		val observer = Firestore.observeDocuments("users")
//		val handler = Handler()
//		handler.postDelayed({ observer.remove() }, 10000)

		tickerData = bitcoinRepository.getTicker(source.get()!!, coin.get()!!, currency.get()!!)

		// refresh ticker on changes
		source.observe { refreshTicker() }
		currency.observe { refreshTicker() }
		coin.observe { refreshTicker() }

	}

	override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
		this.lifecycleOwner = lifecycleOwner
		refreshTicker()
	}

	private fun refreshTicker() {
		lifecycleOwner?.let {
			tickerData.removeObservers(it)
			tickerData = bitcoinRepository.getTicker(source.get()!!, coin.get()!!, currency.get()!!)
			tickerData.observe(it, ticker)
		}
	}


	fun logout() {
		FirebaseAuth.getInstance().signOut()
	}

}