package com.strv.dundee.ui.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.strv.dundee.repo.BitcoinRepository
import com.strv.dundee.model.BitcoinSource
import com.strv.dundee.model.Coin
import com.strv.dundee.model.Currency
import com.strv.dundee.model.Ticker
import com.strv.ktools.LifecycleReceiver
import com.strv.ktools.inject
import com.strv.ktools.observe

class MainViewModel() : ViewModel(), LifecycleReceiver {
    val bitcoinRepository by inject<BitcoinRepository>()
    val ticker = ObservableField<Ticker>()
    val currency = ObservableField<String>(Currency.USD)
    val coin = ObservableField<String>(Coin.BTC)

    override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
        bitcoinRepository.getTicker(BitcoinSource.BITSTAMP, coin.get()!!, currency.get()!!).observe(lifecycleOwner, ticker)
    }
}