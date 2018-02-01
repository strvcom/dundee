package com.strv.dundee.ui.charts

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentChartsBinding
import com.strv.dundee.ui.main.MainViewModel
import com.strv.ktools.vmb

interface ChartsView {

}

class ChartsFragment : Fragment(), ChartsView {

	companion object {
		fun newInstance() = ChartsFragment().apply { arguments = Bundle() }
	}

	private val vmb by vmb<ChartsViewModel, FragmentChartsBinding>(R.layout.fragment_charts) { ChartsViewModel(ViewModelProviders.of(activity!!).get(MainViewModel::class.java)) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = vmb.rootView

}