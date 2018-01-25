package com.strv.dundee.ui.walletdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivityWalletDetailBinding
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.vmb

interface WalletDetailView {
}

class WalletDetailActivity : BaseActivity(), WalletDetailView {

	companion object {

		const val EXTRA_WALLET_OVERVIEW = "wallet_overview"

		fun newIntent(context: Context, wallet: WalletOverview) = Intent(context, WalletDetailActivity::class.java).apply {
			putExtra(EXTRA_WALLET_OVERVIEW, wallet)
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<WalletDetailViewModel, ActivityWalletDetailBinding>(R.layout.activity_wallet_detail) { WalletDetailViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)
		setToolbarTitle(intent.getParcelableExtra<WalletOverview>(EXTRA_WALLET_OVERVIEW).coin)
	}
}
