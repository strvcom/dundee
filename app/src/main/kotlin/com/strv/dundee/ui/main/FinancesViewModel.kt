package com.strv.dundee.ui.main

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.common.TouchHelperCallback
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.EventLiveData
import com.strv.ktools.Resource
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


class FinancesViewModel(mainViewModel: MainViewModel) : ViewModel() {
	val walletRemovedSnackBar = EventLiveData<Wallet>()
	val walletOpened = EventLiveData<Wallet>()

	private val application by inject<Application>()
	private val bitcoinRepository by inject<BitcoinRepository>()
	private val userRepository by inject<UserRepository>()
	private val walletRepository by inject<WalletRepository>()

	private val itemClickCallback = object : OnItemClickListener<Wallet> {
		override fun onItemClick(item: Wallet) {
			walletOpened.publish(item)
		}
	}

	val touchHelperCallback = object : TouchHelperCallback<Wallet>(R.id.item_foreground, R.id.ic_left, R.id.ic_right) {
		override fun onItemSwiped(item: Wallet) {
			removeWallet(item)
			walletRemovedSnackBar.publish(item)
		}
	}

	val itemBinding = ItemBinding.of<Wallet>(BR.item, R.layout.item_wallet).bindExtra(BR.viewModel, this).bindExtra(BR.listener, itemClickCallback)!!
	var wallets: DiffObservableListLiveData<Wallet>
	var user = userRepository.getCurrentUserData()
	val tickers = HashMap<String, LiveData<Resource<Ticker>>>()
	val source = mainViewModel.source
	val currency = mainViewModel.currency
	val totalValue = MediatorLiveData<Double>()

	init {
		// compose Ticker LiveData (observed by data binding automatically)
		refreshTicker()

		// refresh ticker on input changes
		source.observeForever { refreshTicker() }
		currency.observeForever { refreshTicker() }

		wallets = DiffObservableListLiveData(walletRepository.getWalletsForCurrentUser(), object : DiffObservableList.Callback<Wallet> {
			override fun areContentsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
		})

		// add total value calculation and attach to ticker and wallets LiveData
		totalValue.addValueSource(wallets, { recalculateTotal() })
		tickers.forEach { totalValue.addValueSource(it.value, { recalculateTotal() }) }
	}

	private fun refreshTicker() {
		Coin.getAll().forEach { tickers[it] = bitcoinRepository.getTicker(source.value!!, it, currency.value!!, liveDataToReuse = tickers[it]) }
	}

	private fun recalculateTotal(): Double = wallets.value?.data?.sumByDouble { tickers[it.coin]?.value?.data?.getValue(it.amount ?: 0.toDouble()) ?: 0.toDouble() } ?: 0.toDouble()

	fun addWallet(wallet: Wallet) {
		walletRepository.addWalletToCurrentUser(wallet)
	}

	private fun removeWallet(wallet: Wallet) {
		walletRepository.removeWallet(wallet)
	}

}