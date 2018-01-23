package com.strv.dundee.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentDashboardBinding
import com.strv.dundee.model.entity.WalletOverview
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb


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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}
}