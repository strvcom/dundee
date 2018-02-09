package com.strv.dundee.test.base

import android.arch.lifecycle.MutableLiveData
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Coin
import com.strv.dundee.model.entity.Currency
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.Ticker
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.model.repo.TickerLiveData
import com.strv.dundee.ui.dashboard.DashboardViewModel
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.DiffObservableListLiveData
import com.strv.ktools.Resource
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList
import java.util.Date

object TestViewModelProvider {

	fun getMainViewModel(): MainViewModel {
		val viewModel = MainViewModel()
		val rates = hashMapOf<String, Double>()
		rates[Currency.USD] = 1.0
		rates[Currency.CZK] = 20.5
		rates[Currency.EUR] = 0.8
		viewModel.exchangeRates.value = Resource(Resource.Status.SUCCESS, ExchangeRates(Currency.USD, Date(1518095973000), rates))
		return viewModel
	}

	fun getDashboardViewModel(): DashboardViewModel {
		val dashboardViewModel = DashboardViewModel(getMainViewModel())
		val coinWallets = MutableLiveData<Resource<List<WalletOverview>>>()
		val coinWalletsList = mutableListOf<WalletOverview>()
		val boughtPrices = mutableListOf<Pair<String, Double>>()
		boughtPrices.add(Pair(Currency.CZK, 50000.5))
		boughtPrices.add(Pair(Currency.USD, 300.2))
		boughtPrices.add(Pair(Currency.EUR, 400.8))
		coinWalletsList.add(WalletOverview(Coin.BTC, 1.5, boughtPrices))
		coinWalletsList.add(WalletOverview(Coin.LTC, 200.0, boughtPrices))
		coinWalletsList.add(WalletOverview(Coin.ETH, 340.99, boughtPrices))
		coinWalletsList.add(WalletOverview(Coin.BCH, 12.87, boughtPrices))
		coinWalletsList.add(WalletOverview(Coin.XRP, 23.5, boughtPrices))
		coinWallets.value = Resource(Resource.Status.SUCCESS, coinWalletsList)
		dashboardViewModel.wallets = DiffObservableListLiveData(coinWallets, object : DiffObservableList.Callback<WalletOverview> {
			override fun areItemsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = true
			override fun areContentsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = true
		})

		dashboardViewModel.tickers[Coin.BTC] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.CZK, Coin.BTC, 200500.8, 234500.8, 180990.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.BCH] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.EUR, Coin.BCH, 840.8, 934.8, 753.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.ETH] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.ETH, 756.8, 845.8, 654.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.LTC] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.CZK, Coin.LTC, 3568.8, 4569.8, 3214.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.XRP] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.EUR, Coin.XRP, 0.8, 1.2, 0.44, 1518095973000)) }
		dashboardViewModel.currency.value = Currency.USD

		return dashboardViewModel
	}
}