package com.strv.dundee.ui.dashboard

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
import com.strv.dundee.databinding.FragmentDashboardBinding
import com.strv.dundee.databinding.FragmentDashboardEmptyBinding
import com.strv.dundee.model.entity.WalletOverview
import com.strv.dundee.ui.main.MainViewModel
import com.strv.dundee.ui.wallet.EditWalletAmountActivity
import com.strv.dundee.ui.wallet.WalletDetailActivity
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.Resource
import com.strv.ktools.vmb
import cz.kinst.jakub.view.SimpleStatefulLayout

interface DashboardView {
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<WalletOverview> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
}

class DashboardFragment : Fragment(), DashboardView {

	companion object {
		fun newInstance() = DashboardFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<WalletOverview>(this)

	private val vmb by vmb<DashboardViewModel, FragmentDashboardBinding>(R.layout.fragment_dashboard) { DashboardViewModel(ViewModelProviders.of(activity as FragmentActivity).get(MainViewModel::class.java)) }
	private var snackbarVisible = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.walletOpened.observe(this, Observer { wallet ->
			context?.let { ctx -> wallet?.let { startActivity(WalletDetailActivity.newIntent(ctx, it)) } }
		})

		vmb.viewModel.tickers.forEach {
			it.value.observe(this, Observer {
				if (it?.status == Resource.Status.NO_CONNECTION && !snackbarVisible) Snackbar.make(vmb.rootView, R.string.no_internet_connection, Snackbar.LENGTH_SHORT).apply {
					addCallback(object : Snackbar.Callback() {
						override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
							super.onDismissed(transientBottomBar, event)
							snackbarVisible = false
						}
					})
					snackbarVisible = true
					show()
				}
			})
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val emptyView = FragmentDashboardEmptyBinding.inflate(LayoutInflater.from(activity))
		emptyView!!.onClickListener = View.OnClickListener { addAmount() }
		vmb.binding.stateful.setStateView(SimpleStatefulLayout.State.EMPTY, emptyView.root)
	}

	fun addAmount() {
		context?.let { startActivity(EditWalletAmountActivity.newIntent(it)) }
	}
}