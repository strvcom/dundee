package com.strv.dundee.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivityAddAmountBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.vmb

interface AddAmountView {
}

class AddAmountActivity : BaseActivity(), AddAmountView {

	companion object {
		const val EXTRA_WALLET = "wallet"

		fun newIntent(context: Context) = Intent(context, AddAmountActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
		fun newIntent(context: Context, wallet: Wallet) = Intent(context, AddAmountActivity::class.java).apply {
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			putExtra(EXTRA_WALLET, wallet)}
	}

	private val vmb by vmb<AddAmountViewModel, ActivityAddAmountBinding>(R.layout.activity_add_amount) { AddAmountViewModel(intent.getParcelableExtra(EXTRA_WALLET)) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)
		if(intent.getParcelableExtra<Wallet>(EXTRA_WALLET) != null) setToolbarTitle(R.string.edit_amount)

		vmb.viewModel.finish.observe(this, Observer {
			setResult(Activity.RESULT_OK)
			finish()
		})
	}
}
