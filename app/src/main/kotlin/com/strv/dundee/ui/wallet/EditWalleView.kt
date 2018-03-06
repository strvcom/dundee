package com.strv.dundee.ui.wallet

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivityEditWalletBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.vmb
import java.util.Date

interface EditWalletView {
	fun pickBoughtDate()
}

class EditWalletActivity : BaseActivity(), EditWalletView {

	companion object {
		const val EXTRA_WALLET = "wallet"

		fun newIntent(context: Context) = Intent(context, EditWalletActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
		fun newIntent(context: Context, wallet: Wallet) = Intent(context, EditWalletActivity::class.java).apply {
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			putExtra(EXTRA_WALLET, wallet)
		}
	}

	private val vmb by vmb<EditWalletViewModel, ActivityEditWalletBinding>(R.layout.activity_edit_wallet) { EditWalletViewModel(intent.getParcelableExtra(EXTRA_WALLET)) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)
		if (intent.getParcelableExtra<Wallet>(EXTRA_WALLET) != null) setToolbarTitle(R.string.investments_edit)

		vmb.viewModel.finish.observe(this, Observer {
			setResult(Activity.RESULT_OK)
			finish()
		})
	}

	override fun pickBoughtDate() {
		supportFragmentManager?.let {
			val dialog = DatePickerDialogFragment.newInstance(vmb.viewModel.boughtOn.value!!)
			dialog.listener = object : DatePickerDialogFragment.DatePickerListener {
				override fun onDateSelected(date: Date) {
					vmb.viewModel.boughtOn.value = date
				}
			}
			dialog.show(it, dialog.javaClass.name)
		}
	}
}
