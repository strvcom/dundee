package com.strv.dundee.ui.nav

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.strv.dundee.R
import com.strv.dundee.ui.charts.ChartsFragment
import com.strv.dundee.ui.dashboard.DashboardFragment
import com.strv.dundee.ui.investments.InvestmentsFragment

class MainNavigation {

	enum class Section(@StringRes val titleResId: Int, @DrawableRes val iconResId: Int, val tag: String, val fragmentConstructor: () -> Fragment) {
		DASHBOARD(R.string.nav_dashboard, R.drawable.ic_dashboard, DashboardFragment::class.java.simpleName, DashboardFragment.Companion::newInstance),
		CHARTS(R.string.nav_charts, R.drawable.ic_graph, ChartsFragment::class.java.simpleName, ChartsFragment.Companion::newInstance),
		INVESTMENTS(R.string.nav_investments, R.drawable.ic_investments, InvestmentsFragment::class.java.simpleName, InvestmentsFragment.Companion::newInstance);
	}

	val currentTab = MutableLiveData<Section>()

	fun goToDashboard() {
		goTo(Section.DASHBOARD)
	}

	fun goToGraphs() {
		goTo(Section.CHARTS)
	}

	fun goToInvestments() {
		goTo(Section.INVESTMENTS)
	}

	fun goTo(tab: Section) {
		if (currentTab.value != tab) currentTab.value = tab
	}
}