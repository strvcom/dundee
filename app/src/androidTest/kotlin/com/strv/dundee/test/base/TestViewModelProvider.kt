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
		dashboardViewModel.wallets = DiffObservableListLiveData(coinWallets, object : DiffObservableList.Callback<WalletOverview> {
			override fun areItemsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = true
			override fun areContentsTheSame(oldItem: WalletOverview?, newItem: WalletOverview?) = true
		})

		dashboardViewModel.tickers[Coin.BTC] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.BTC, 6000.8, 8030.8, 5467.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.BCH] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.BCH, 6000.8, 8030.8, 5467.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.ETH] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.ETH, 6000.8, 8030.8, 5467.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.LTC] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.LTC, 6000.8, 8030.8, 5467.44, 1518095973000)) }
		dashboardViewModel.tickers[Coin.XRP] = TickerLiveData().apply { value = Resource(Resource.Status.SUCCESS, Ticker(BitcoinSource.BITFINEX, Currency.USD, Coin.XRP, 6000.8, 8030.8, 5467.44, 1518095973000)) }
		dashboardViewModel.currency.value = Currency.USD

		return dashboardViewModel
	}
}