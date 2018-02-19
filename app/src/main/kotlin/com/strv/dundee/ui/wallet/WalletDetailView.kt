package com.strv.dundee.ui.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivityWalletDetailBinding
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb

interface WalletDetailView {
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<Wallet> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
}

class WalletDetailActivity : BaseActivity(), WalletDetailView {

	companion object {

		const val EXTRA_WALLET_OVERVIEW = "wallet_overview"
		const val EXTRA_EXCHANGE_RATES = "exchange_rates"
		const val EXTRA_CURRENCY = "currency"

		fun newIntent(context: Context, wallet: WalletOverview, exchangeRates: ExchangeRates, currency: String) = Intent(context, WalletDetailActivity::class.java).apply {
			putExtra(EXTRA_WALLET_OVERVIEW, wallet)
			putExtra(EXTRA_EXCHANGE_RATES, exchangeRates)
			putExtra(EXTRA_CURRENCY, currency)
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		}
	}

	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<Wallet>(this)

	private val vmb by vmb<WalletDetailViewModel, ActivityWalletDetailBinding>(R.layout.activity_wallet_detail) {
		WalletDetailViewModel(intent.getParcelableExtra(EXTRA_WALLET_OVERVIEW), intent.getParcelableExtra(EXTRA_EXCHANGE_RATES), intent.getStringExtra(EXTRA_CURRENCY))
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)
		setToolbarTitle(intent.getParcelableExtra<WalletOverview>(EXTRA_WALLET_OVERVIEW).coin)
	}
}
