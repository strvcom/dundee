package com.strv.dundee.ui.wallet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentWalletsBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb

interface WalletsView {
	fun addAmount()
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<Wallet> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
}

class WalletsFragment : Fragment(), WalletsView {

	companion object {
		private const val ACTION_ADD_AMOUNT = 1

		fun newInstance() = WalletsFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<Wallet>(this)

	private val vmb by vmb<WalletsViewModel, FragmentWalletsBinding>(R.layout.fragment_wallets) { WalletsViewModel(ViewModelProviders.of(activity as FragmentActivity).get(MainViewModel::class.java)) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.walletRemovedSnackBar.observe(this, Observer { wallet ->
			Snackbar.make(vmb.rootView, R.string.item_removed, Snackbar.LENGTH_SHORT).apply {
				setAction(R.string.undo, { wallet?.let { vmb.viewModel.addWallet(it) } })
				show()
			}
		})
		vmb.viewModel.walletOpened.observe(this, Observer { it?.let { wallet -> activity?.let { startActivity(EditWalletAmountActivity.newIntent(it, wallet)) } } })
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}

	override fun addAmount() {
		context?.let { startActivityForResult(EditWalletAmountActivity.newIntent(it), ACTION_ADD_AMOUNT) }
	}
}