package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.DiffObservableListLiveData
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.User
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.firestore.FirestoreDocumentQueryLiveData
import com.strv.dundee.model.firestore.FirestoreDocumentsLiveData
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.common.Resource
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


class MainViewModel() : ViewModel() {
	val application by inject<Application>()

	private val bitcoinRepository by inject<BitcoinRepository>()

	var wallets: DiffObservableListLiveData<Wallet>
	var user: LiveData<Resource<User>>
	val tickers = HashMap<String, LiveData<Resource<Ticker>>>()
	val itemBinding: ItemBinding<Wallet> = ItemBinding.of<Wallet>(BR.item, R.layout.item_wallet).bindExtra(BR.viewModel, this)
	val source by application.sharedPrefs().stringLiveData(BitcoinSource.BITSTAMP)
	val currency by application.sharedPrefs().stringLiveData(Currency.USD)
	val sourceAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, BitcoinSource.getAll())
	val currencyAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Currency.getAll())
	val coinAdapter = ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, Coin.getAll())
	val totalValue = MediatorLiveData<Double>()

	init {
		// compose Ticker LiveData (observed by data binding automatically)
		refreshTicker()

		// refresh ticker on input changes
		source.observeForever { refreshTicker() }
		currency.observeForever { refreshTicker() }

		wallets = DiffObservableListLiveData(FirestoreDocumentsLiveData(FirebaseFirestore.getInstance().collection(Wallet.COLLECTION).whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid), Wallet::class.java), object : DiffObservableList.Callback<Wallet> {
			override fun areContentsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
		})

		// add total value calculation and attach to ticker and wallets LiveData

		totalValue.addValueSource(wallets, { recalculateTotal() })
		tickers.forEach {
			totalValue.addValueSource(it.value, { recalculateTotal() })
		}


		user = FirestoreDocumentQueryLiveData(FirebaseFirestore.getInstance().collection(User.COLLECTION).whereEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid), User::class.java)
	}

	private fun refreshTicker() {
		Coin.getAll().forEach { tickers[it] = bitcoinRepository.getTicker(source.value!!, it, currency.value!!, liveDataToReuse = tickers[it]) }
	}

	private fun recalculateTotal(): Double = wallets.value?.data?.sumByDouble { tickers[it.coin]?.value?.data?.getValue(it.amount ?: 0.toDouble()) ?: 0.toDouble() } ?: 0.toDouble()

	fun logout() {
		FirebaseAuth.getInstance().signOut()
	}

}