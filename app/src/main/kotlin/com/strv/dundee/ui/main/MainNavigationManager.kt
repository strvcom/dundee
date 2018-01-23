package com.strv.dundee.ui.main

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.strv.dundee.R

class MainNavigationManager {

	enum class Section(@StringRes val titleResId: Int, @DrawableRes val iconResId: Int, val tag: String, val fragmentConstructor: () -> Fragment) {
		DASHBOARD(R.string.dashboard, R.drawable.ic_dashboard, DashboardFragment::class.java.simpleName, DashboardFragment.Companion::newInstance),
		GRAPHS(R.string.graphs, R.drawable.ic_graph, GraphsFragment::class.java.simpleName, GraphsFragment.Companion::newInstance),
		FINANCES(R.string.finances, R.drawable.ic_finances, FinancesFragment::class.java.simpleName, FinancesFragment.Companion::newInstance);
	}

	val currentTab = MutableLiveData<Section>()

	fun goToDashboard() {
		goTo(Section.DASHBOARD)
	}

	fun goToGraphs() {
		goTo(Section.GRAPHS)
	}

	fun gotToFinances() {
		goTo(Section.FINANCES)
	}

	fun goTo(tab: Section) {
		if (currentTab.value != tab) currentTab.value = tab
	}
}