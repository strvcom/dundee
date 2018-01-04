package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.firestore.FirestoreLiveData
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.common.Resource
import com.strv.ktools.inject
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData
import me.tatarka.bindingcollectionadapter2.ItemBinding


class MainViewModel() : ViewModel() {
	val application by inject<Application>()

	private val bitcoinRepository by inject<BitcoinRepository>()

	val itemBinding: ItemBinding<Wallet> = ItemBinding.of(BR.item, R.layout.item_wallet)
	var wallets: LiveData<Resource<List<Wallet>>>
	lateinit var ticker: LiveData<Resource<Ticker>>
	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val coin by application.sharedPrefs().stringLiveData(Coin.BTC)
	val sourceAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, BitcoinSource.getAll())
	val currencyAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Currency.getAll())
	val coinAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Coin.getAll())

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

		// compose Ticker LiveData (observed by data binding automatically)
		refreshTicker()

		// refresh ticker on input changes
		source.observeForever { refreshTicker() }
		currency.observeForever { refreshTicker() }
		coin.observeForever { refreshTicker() }

		wallets = FirestoreLiveData(FirebaseFirestore.getInstance().collection(Wallet.COLLECTION).whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid), Wallet::class.java)
	}


	private inline fun refreshTicker() {
		ticker = bitcoinRepository.getTicker(source.value!!, coin.value!!, currency.value!!)
	}


	fun logout() {
		FirebaseAuth.getInstance().signOut()
	}

}