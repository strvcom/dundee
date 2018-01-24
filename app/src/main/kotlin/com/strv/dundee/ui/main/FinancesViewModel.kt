package com.strv.dundee.ui.main

import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.common.TouchHelperCallback
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.EventLiveData
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


class FinancesViewModel(mainViewModel: MainViewModel) : ViewModel() {
	val walletRemovedSnackBar = EventLiveData<Wallet>()
	val walletOpened = EventLiveData<Wallet>()

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

	val itemBinding = ItemBinding.of<Wallet>(BR.item, R.layout.item_finances).bindExtra(BR.viewModel, this).bindExtra(BR.listener, itemClickCallback)!!
	var wallets: DiffObservableListLiveData<Wallet>

	init {
		wallets = DiffObservableListLiveData(walletRepository.getWalletsForCurrentUser(), object : DiffObservableList.Callback<Wallet> {
			override fun areContentsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
		})
	}

	fun addWallet(wallet: Wallet) {
		walletRepository.addWalletToCurrentUser(wallet)
	}

	private fun removeWallet(wallet: Wallet) {
		walletRepository.removeWallet(wallet)
	}

}