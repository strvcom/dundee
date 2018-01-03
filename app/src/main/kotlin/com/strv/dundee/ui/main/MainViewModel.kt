package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
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
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData


class MainViewModel() : ViewModel(), LifecycleReceiver {
	val application by inject<Application>()
	var lifecycleOwner: LifecycleOwner? = null

	val bitcoinRepository by inject<BitcoinRepository>()
	var ticker: LiveData<Resource<Ticker>>
	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val coin by application.sharedPrefs().stringLiveData(Coin.BTC)

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

		ticker = bitcoinRepository.getTicker(source.value!!, coin.value!!, currency.value!!)


	}

	override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
		this.lifecycleOwner = lifecycleOwner
		refreshTicker()

//		// refresh ticker on changes
		source.observe(lifecycleOwner, Observer { refreshTicker() })
		currency.observe(lifecycleOwner, Observer { refreshTicker() })
		coin.observe(lifecycleOwner, Observer { refreshTicker() })
	}

	private fun refreshTicker() {
		lifecycleOwner?.let {
			ticker.removeObservers(it)
			ticker = bitcoinRepository.getTicker(source.value!!, coin.value!!, currency.value!!)
		}
	}


	fun logout() {
		FirebaseAuth.getInstance().signOut()
	}

}