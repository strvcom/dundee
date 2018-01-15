package com.strv.dundee.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.dundee.R
import com.strv.dundee.databinding.FragmentGraphsBinding
import com.strv.ktools.vmb


interface GraphsView {

}

class GraphsFragment : Fragment(), GraphsView {

	companion object {
		fun newInstance() = GraphsFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	private val vmb by vmb<GraphsViewModel, FragmentGraphsBinding>(R.layout.fragment_graphs) { GraphsViewModel() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return vmb.rootView
	}
}