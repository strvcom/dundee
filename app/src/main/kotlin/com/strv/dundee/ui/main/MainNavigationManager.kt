package com.strv.dundee.ui.main

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import com.strv.dundee.R


class MainNavigationManager {

	enum class BottomBarTab(val id: Int, val tag: String) {
		DASHBOARD(R.id.action_dashboard, DashboardFragment::class.java.simpleName),
		GRAPHS(R.id.action_graphs, GraphsFragment::class.java.simpleName),
		FINANCES(R.id.action_finances, FinancesFragment::class.java.simpleName);

		companion object {
			fun get(id: Int) = BottomBarTab.values().find { it.id == id } ?: DASHBOARD
		}
	}

	val currentFragment = MutableLiveData<BottomBarTab>()

	fun goTo(tab: BottomBarTab) {
		currentFragment.value = tab
	}

	fun showFragment(activity: AppCompatActivity, @IdRes containerId: Int) {
		detachAll(activity)

		val transaction = activity.supportFragmentManager.beginTransaction()
		var fragment = activity.supportFragmentManager.findFragmentByTag(currentFragment.value?.tag)
		if (fragment == null) {
			when (currentFragment.value) {
				BottomBarTab.DASHBOARD -> fragment = DashboardFragment.newInstance()
				BottomBarTab.GRAPHS -> fragment = GraphsFragment.newInstance()
				BottomBarTab.FINANCES -> fragment = FinancesFragment.newInstance()
			}
			transaction.add(containerId, fragment, currentFragment.value?.tag)
		} else {
			transaction.attach(fragment)
		}
		transaction.commitAllowingStateLoss()
	}

	private fun detachAll(activity: AppCompatActivity) {
		BottomBarTab.values().forEach { activity.supportFragmentManager.findFragmentByTag(it.tag)?.let { activity.supportFragmentManager.beginTransaction().detach(it).commitNowAllowingStateLoss() } }
	}
}