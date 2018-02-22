package com.strv.dundee.ui.wallet

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.common.daysToNow
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.TimeFrame
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList

class WalletDetailViewModel(val walletOverview: WalletOverview, val exchangeRates: ExchangeRates, val currency: String) : ViewModel() {

	private val walletRepository by inject<WalletRepository>()
	private val historyRepository by inject<BitcoinRepository>()

	private val itemClickCallback = object : OnItemClickListener<Wallet> {
		override fun onItemClick(item: Wallet) {
		}
	}

	val itemBinding = ItemBinding.of<Wallet>(BR.item, R.layout.item_wallet_detail)
		.bindExtra(BR.viewModel, this)
		.bindExtra(BR.listener, itemClickCallback)!!

	var wallets: DiffObservableListLiveData<Wallet>
	val history = historyRepository.getHistory(walletOverview.coin, getTimeFrame())
	val historicalProfit = MediatorLiveData<List<Entry>>()

	init {
		wallets = DiffObservableListLiveData(walletRepository.getWalletsForCurrentUser(walletOverview.coin), object : DiffObservableList.Callback<Wallet> {
			override fun areContentsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: Wallet?, newItem: Wallet?) = oldItem == newItem
		})

		historicalProfit.addValueSource(history, { getHistoricalProfit() })
	}

	private fun getHistoricalProfit(): List<Entry> {
		val result = mutableListOf<Entry>()
		val boughtPrice = walletOverview.getBoughtPrice(Currency.USD, exchangeRates)
		history.value?.data?.prices?.forEach {
			if (walletOverview.firstWalletBoughtDate != null && it.timestamp > walletOverview.firstWalletBoughtDate!!.time)
				result.add(Entry(it.timestamp.toFloat(), (it.price * walletOverview.amount - boughtPrice).toFloat()))
		}

		var i = 0
		while (i < result.size-1) {
			if ((result[i].y < 0 && result[i + 1].y > 0) || result[i].y > 0 && result[i + 1].y < 0) {
				val x = result[i].x + ((0 - result[i].y) * (result[i+1].x - result[i].x)) / (result[i+1].y - result[i].y)
				result.add(i+1, Entry(x, 0f))
			}
			i++
		}

		return result
	}

	private fun getTimeFrame() = walletOverview.firstWalletBoughtDate?.let {
		when {
			it.daysToNow() < 2 -> TimeFrame.DAY
			it.daysToNow() < 8 -> TimeFrame.WEEK
			it.daysToNow() < 32 -> TimeFrame.MONTH
			it.daysToNow() < 94 -> TimeFrame.QUARTER
			it.daysToNow() < 187 -> TimeFrame.HALF
			it.daysToNow() < 366 -> TimeFrame.YEAR
			else -> TimeFrame.ALL
		}
	}
		?: TimeFrame.ALL

}