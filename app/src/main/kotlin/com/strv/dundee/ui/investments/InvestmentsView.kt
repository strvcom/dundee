package com.strv.dundee.ui.investments

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
import com.strv.dundee.databinding.FragmentInvestmentsBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.ui.main.MainViewModel
import com.strv.dundee.ui.wallet.EditWalletActivity
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb

interface InvestmentsView {
	fun addAmount()
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<Wallet> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
}

class InvestmentsFragment : Fragment(), InvestmentsView {

	companion object {
		private const val ACTION_ADD_AMOUNT = 1

		fun newInstance() = InvestmentsFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<Wallet>(this)

	private val vmb by vmb<InvestmentsViewModel, FragmentInvestmentsBinding>(R.layout.fragment_investments) { InvestmentsViewModel(ViewModelProviders.of(activity as FragmentActivity).get(MainViewModel::class.java)) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.walletRemovedSnackBar.observe(this, Observer { wallet ->
			Snackbar.make(vmb.rootView, R.string.global_item_removed, Snackbar.LENGTH_SHORT).apply {
				setAction(R.string.global_undo, { wallet?.let { vmb.viewModel.addWallet(it) } })
				show()
			}
		})
		vmb.viewModel.walletOpened.observe(this, Observer { it?.let { wallet -> activity?.let { startActivity(EditWalletActivity.newIntent(it, wallet)) } } })
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}

	override fun addAmount() {
		context?.let { startActivityForResult(EditWalletActivity.newIntent(it), ACTION_ADD_AMOUNT) }
	}
}