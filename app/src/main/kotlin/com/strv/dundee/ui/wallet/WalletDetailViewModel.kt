package com.strv.dundee.ui.wallet

import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList

class WalletDetailViewModel(walletOverview: WalletOverview) : ViewModel() {

	private val walletRepository by inject<WalletRepository>()

	private val itemClickCallback = object : OnItemClickListener<Wallet> {
		override fun onItemClick(item: Wallet) {
		}
	}

	val itemBinding = ItemBinding.of<Wallet>(BR.item, R.layout.item_wallet_detail)
		.bindExtra(BR.viewModel, this)
		.bindExtra(BR.listener, itemClickCallback)!!

	var wallets: DiffObservableListLiveData<Wallet>

	init {
		wallets = DiffObservableListLiveData(walletRepository.getWalletsForCurrentUser(walletOverview.coin), object : DiffObservableList.Callback<Wallet> {
			override fun areContentsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
		})
	}
}