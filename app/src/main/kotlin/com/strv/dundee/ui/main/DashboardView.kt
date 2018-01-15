package com.strv.dundee.ui.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentDashboardBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb


interface DashboardView {
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<Wallet> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
}

class DashboardFragment : Fragment(), DashboardView {

	companion object {
		fun newInstance() = DashboardFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<Wallet>(this)

	private val vmb by vmb<DashboardViewModel, FragmentDashboardBinding>(R.layout.fragment_dashboard) { DashboardViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.walletRemovedSnackBar.observe(this, Observer { wallet ->
			val snackbar = Snackbar.make(vmb.rootView, R.string.item_removed, Snackbar.LENGTH_SHORT)
			snackbar.setAction(R.string.undo, { wallet?.let { vmb.viewModel.addWallet(it) } })
			snackbar.show()
		})
		vmb.viewModel.walletOpened.observe(this, Observer { it?.let { wallet -> activity?.let { startActivity(AddAmountActivity.newIntent(it, wallet)) } } })
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}
}