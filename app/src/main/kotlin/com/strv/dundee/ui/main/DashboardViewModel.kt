package com.strv.dundee.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.strv.dundee.BR
import com.strv.dundee.R
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.ExchangeRate
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.BitcoinRepository
import com.strv.dundee.model.repo.ExchangeRateRepository
import com.strv.dundee.model.repo.WalletRepository
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.Resource
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList

class DashboardViewModel(mainViewModel: MainViewModel) : ViewModel() {

	private val bitcoinRepository by inject<BitcoinRepository>()
	private val walletRepository by inject<WalletRepository>()
	private val exchangeRateRepository by inject<ExchangeRateRepository>()

	val itemBinding = ItemBinding.of<WalletOverview>(BR.item, R.layout.item_wallet_dashboard).bindExtra(BR.viewModel, this)!!
	var wallets: DiffObservableListLiveData<WalletOverview>
	val tickers = HashMap<String, LiveData<Resource<Ticker>>>()
	val source = mainViewModel.source
	val currency = mainViewModel.currency
	val apiCurrency = mainViewModel.apiCurrency
	val totalValue = MediatorLiveData<Double>()
	val totalProfit = MediatorLiveData<Double>()
	var exchangeRate: LiveData<Resource<ExchangeRate>>? = null        // used for prizes calculation, based on API currency and UI currency
	var usdExchangeRate: LiveData<Resource<ExchangeRate>>? = null    // used for profit calculation, boughtPrice is in USD

	init {
		// compose Ticker and exchange rates LiveData (observed by data binding automatically)
		refreshTicker()
		refreshExchangeRate()
		refreshUsdExchangeRate()

		// refresh ticker and exchange rates on input changes
		currency.observeForever {
			refreshExchangeRate()
			refreshUsdExchangeRate()
		}
		apiCurrency.observeForever {
			refreshTicker()
			refreshExchangeRate()
		}
		source.observeForever { refreshTicker() }

		// used for transforming Wallet list to WalletOverview list
		val coinWallets = MediatorLiveData<Resource<List<WalletOverview>>>().addValueSource(walletRepository.getWalletsForCurrentUser(), {
			val result = hashMapOf<String, WalletOverview>()
			Coin.getAll().forEach { result[it] = WalletOverview(it) }
			it?.data?.fold(result, { accumulator, wallet ->
				accumulator[wallet.coin!!]!!.amount += wallet.amount!!
				accumulator[wallet.coin!!]!!.boughtPrice += wallet.boughtPrice!!
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
		totalValue.addValueSource(exchangeRate!!, { recalculateTotal() })
		tickers.forEach { totalValue.addValueSource(it.value, { recalculateTotal() }) }
		totalProfit.addValueSource(totalValue, { recalculateTotalProfit() })
		totalProfit.addValueSource(usdExchangeRate!!, { recalculateTotalProfit() })
	}

	private fun refreshTicker() {
		Coin.getAll().forEach { tickers[it] = bitcoinRepository.getTicker(source.value!!, it, apiCurrency.value!!, liveDataToReuse = tickers[it]) }
	}

	private fun refreshExchangeRate() {
		exchangeRate = exchangeRateRepository.getExchangeRate(apiCurrency.value!!, currency.value!!, exchangeRate)
	}

	private fun refreshUsdExchangeRate() {
		usdExchangeRate = exchangeRateRepository.getExchangeRate(Currency.USD, currency.value!!, usdExchangeRate)
	}

	private fun recalculateTotal(): Double =
		if (apiCurrency.value == exchangeRate!!.value?.data?.source)
			wallets.value?.data?.sumByDouble {
				if (tickers[it.coin]?.value?.data?.currency == apiCurrency.value)
					(tickers[it.coin]?.value?.data?.getValue(it.amount) ?: 0.0) * (exchangeRate?.value?.data?.rate ?: 0.0)
				else 0.0
			} ?: 0.0
		else 0.0

	// calculation of current total
	private fun recalculateTotalProfit(): Double =
		if (totalValue.value != null && totalValue.value != 0.0 && currency.value == usdExchangeRate!!.value?.data?.target)
			totalValue.value?.let { it - (wallets.value?.data?.sumByDouble { it.boughtPrice } ?: 0.0) * (usdExchangeRate!!.value?.data?.rate ?: 0.0) } ?: 0.0
		else 0.0
}