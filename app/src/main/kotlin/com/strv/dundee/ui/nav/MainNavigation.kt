package com.strv.dundee.ui.nav

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.strv.dundee.R
import com.strv.dundee.ui.charts.ChartsFragment
import com.strv.dundee.ui.dashboard.DashboardFragment
import com.strv.dundee.ui.wallet.WalletsFragment

class MainNavigation {

	enum class Section(@StringRes val titleResId: Int, @DrawableRes val iconResId: Int, val tag: String, val fragmentConstructor: () -> Fragment) {
		DASHBOARD(R.string.nav_dashboard, R.drawable.ic_dashboard, DashboardFragment::class.java.simpleName, DashboardFragment.Companion::newInstance),
		GRAPHS(R.string.nav_charts, R.drawable.ic_graph, ChartsFragment::class.java.simpleName, ChartsFragment.Companion::newInstance),
		FINANCES(R.string.nav_wallets, R.drawable.ic_finances, WalletsFragment::class.java.simpleName, WalletsFragment.Companion::newInstance);
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