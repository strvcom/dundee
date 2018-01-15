package com.strv.dundee.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentFinancesBinding
import com.strv.ktools.vmb


interface FinancesView {

}

class FinancesFragment : Fragment(), FinancesView {

	companion object {
		fun newInstance() = FinancesFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	private val vmb by vmb<FinancesViewModel, FragmentFinancesBinding>(R.layout.fragment_finances) { FinancesViewModel() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}
}