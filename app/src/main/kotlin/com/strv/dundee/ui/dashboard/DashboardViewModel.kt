package com.strv.dundee.ui.dashboard

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.TickerLiveData
import com.strv.dundee.model.repo.WalletRepository
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.EventLiveData
import com.strv.ktools.Resource
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import cz.kinst.jakub.view.SimpleStatefulLayout
import cz.kinst.jakub.view.StatefulLayout
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList

class DashboardViewModel(mainViewModel: MainViewModel) : ViewModel() {

	private val bitcoinRepository by inject<BitcoinRepository>()
	private val walletRepository by inject<WalletRepository>()

	private val itemClickCallback = object : OnItemClickListener<WalletOverview> {
		override fun onItemClick(item: WalletOverview) {
			walletOpened.publish(item)
		}
	}

	val walletOpened = EventLiveData<WalletOverview>()
	val itemBinding = ItemBinding.of<WalletOverview>(BR.item, R.layout.item_dashboard)
		.bindExtra(BR.viewModel, this)
		.bindExtra(BR.listener, itemClickCallback)!!
	var wallets: DiffObservableListLiveData<WalletOverview>
	val tickers = HashMap<String, TickerLiveData>()
	val source = mainViewModel.source
	val currency = mainViewModel.currency
	val apiCurrency = mainViewModel.apiCurrency
	val exchangeRates = mainViewModel.exchangeRates
	val totalValue = MediatorLiveData<Double>()
	val totalProfit = MediatorLiveData<Double>()
	val state = MediatorLiveData<String>().apply { value = SimpleStatefulLayout.State.PROGRESS }

	init {
		// compose Ticker and exchange rates LiveData (observed by data binding automatically)
		Coin.getAll().forEach { tickers[it] = bitcoinRepository.getTicker(source.value!!, it, apiCurrency.value!!) }

		// setupCached ticker on input changes
		tickers.forEach { (coin, ticker) ->
			ticker.addSource(apiCurrency, { ticker.refresh(source.value!!, coin, apiCurrency.value!!) })
			ticker.addSource(source, { ticker.refresh(source.value!!, coin, apiCurrency.value!!) })
		}

		// used for transforming Wallet list to WalletOverview list
		val coinWallets = MediatorLiveData<Resource<List<WalletOverview>>>().addValueSource(walletRepository.getWalletsForCurrentUser(), {
			val result = hashMapOf<String, WalletOverview>()
			it?.data?.fold(result, { accumulator, wallet ->
				if (accumulator[wallet.coin] == null) accumulator[wallet.coin!!] = WalletOverview(wallet.coin!!)
				accumulator[wallet.coin]!!.amount += wallet.amount!!
				accumulator[wallet.coin]!!.boughtPrices.add(Pair(wallet.boughtCurrency!!, wallet.boughtPrice!!))
				accumulator
			})
			Resource(it?.status ?: Resource.Status.SUCCESS, result.values.toList().sortedByDescending { it.amount })
		})

		wallets = DiffObservableListLiveData(coinWallets, object : DiffObservableList.Callback<WalletOverview> {
			override fun areContentsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = oldItem == newItem
			override fun areItemsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = oldItem == newItem
		})

		// add total value and total profit calculation and attach to ticker, wallets and exchange rates LiveData
		totalValue.addValueSource(currency, { recalculateTotal() })
		totalValue.addValueSource(wallets, { recalculateTotal() })
		totalValue.addValueSource(exchangeRates, { recalculateTotal() })
		tickers.forEach { totalValue.addValueSource(it.value, { recalculateTotal() }) }
		totalProfit.addValueSource(totalValue, { recalculateTotalProfit() })

		state.addValueSource(wallets, { if (it?.data?.isNotEmpty() == true) StatefulLayout.State.CONTENT else SimpleStatefulLayout.State.EMPTY })
	}

	// calculation of current total
	internal fun recalculateTotal(): Double =
		wallets.value?.data?.sumByDouble {
			val fromCurrency = tickers[it.coin]?.value?.data?.currency
			val toCurrency = currency.value
			val amount = tickers[it.coin]?.value?.data?.getValue(it.amount)
			exchangeRates.value?.data?.calculate(fromCurrency, toCurrency, amount) ?: 0.0
		} ?: 0.0

	// calculation of current profit
	internal fun recalculateTotalProfit(): Double {
		val totalBoughtPrice = wallets.value?.data?.sumByDouble { it.getBoughtPrice(currency.value!!, exchangeRates.value?.data) } ?: 0.0
		return (totalValue.value ?: 0.0) - totalBoughtPrice
	}
}