package com.strv.dundee.ui.dashboard

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.common.OnItemClickListener
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.ExchangeRatesLiveData
import com.strv.dundee.model.repo.ExchangeRatesRepository
import com.strv.dundee.model.repo.TickerLiveData
import com.strv.dundee.model.repo.WalletRepository
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.EventLiveData
import com.strv.ktools.Resource
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList

class DashboardViewModel(mainViewModel: MainViewModel) : ViewModel() {

	private val bitcoinRepository by inject<BitcoinRepository>()
	private val walletRepository by inject<WalletRepository>()
	private val exchangeRatesRepository by inject<ExchangeRatesRepository>()

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
	val totalValue = MediatorLiveData<Double>()
	val totalProfit = MediatorLiveData<Double>()
	val exchangeRates = HashMap<String, ExchangeRatesLiveData>()

	init {
		// compose Ticker and exchange rates LiveData (observed by data binding automatically)
		Coin.getAll().forEach { tickers[it] = bitcoinRepository.getTicker(source.value!!, it, apiCurrency.value!!) }
		Currency.getAll().forEach { exchangeRates[it] = exchangeRatesRepository.getExchangeRates(it, Currency.getAll().toList()) }

		// setup ticker and exchange rates on input changes
		currency.observeForever {
			refreshExchangeRates()
		}
		apiCurrency.observeForever {
			refreshTicker()
			refreshExchangeRates()
		}
		source.observeForever { refreshTicker() }

		// used for transforming Wallet list to WalletOverview list
		val coinWallets = MediatorLiveData<Resource<List<WalletOverview>>>().addValueSource(walletRepository.getWalletsForCurrentUser(), {
			val result = hashMapOf<String, WalletOverview>()
			Coin.getAll().forEach { result[it] = WalletOverview(it) }
			it?.data?.fold(result, { accumulator, wallet ->
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
		totalValue.addValueSource(wallets, { recalculateTotal() })
		tickers.forEach { totalValue.addValueSource(it.value, { recalculateTotal() }) }
		exchangeRates.forEach { totalValue.addValueSource(it.value, { recalculateTotal() }) }
		totalProfit.addValueSource(totalValue, { recalculateTotalProfit() })
	}

	private fun refreshTicker() {
		Coin.getAll().forEach { tickers[it]?.refresh(source.value!!, it, apiCurrency.value!!) }
	}

	private fun refreshExchangeRates() {
		Currency.getAll().forEach { exchangeRates[it]?.refresh(it, Currency.getAll().toList()) }
	}

	// calculation of current total
	private fun recalculateTotal(): Double =
		wallets.value?.data?.sumByDouble {
			tickers[it.coin]?.value?.data?.getValue(it.amount, currency.value, exchangeRates) ?: 0.0
		} ?: 0.0

	// calculation of current profit
	private fun recalculateTotalProfit(): Double =
		(totalValue.value
			?: 0.0) - (wallets.value?.data?.sumByDouble { it.getBoughtPrice(currency.value, exchangeRates) } ?: 0.0)
}