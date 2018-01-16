package com.strv.dundee.ui.main

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.strv.dundee.R


class MainNavigationManager {

	enum class BottomBarTab(val id: Int, val tag: String, val fragmentConstructor: () -> Fragment) {
		DASHBOARD(R.id.action_dashboard, DashboardFragment::class.java.simpleName, DashboardFragment.Companion::newInstance),
		GRAPHS(R.id.action_graphs, GraphsFragment::class.java.simpleName, GraphsFragment.Companion::newInstance),
		FINANCES(R.id.action_finances, FinancesFragment::class.java.simpleName, FinancesFragment.Companion::newInstance);

		companion object {
			fun get(id: Int) = BottomBarTab.values().find { it.id == id } ?: DASHBOARD
		}
	}

	val currentTab = MutableLiveData<BottomBarTab>()

	fun goTo(tab: BottomBarTab) {
		if (currentTab.value != tab) currentTab.value = tab
	}

	fun showFragment(activity: AppCompatActivity, @IdRes containerId: Int) {
		currentTab.value?.let {
			detachAll(activity)
			val transaction = activity.supportFragmentManager.beginTransaction()
			var fragment = activity.supportFragmentManager.findFragmentByTag(it.tag)
			if (fragment == null) {
				fragment = it.fragmentConstructor.invoke()
				transaction.add(containerId, fragment, it.tag)
			} else {
				transaction.attach(fragment)
			}
			transaction.commitAllowingStateLoss()
		}
	}

	private fun detachAll(activity: AppCompatActivity) {
		BottomBarTab.values().forEach { activity.supportFragmentManager.findFragmentByTag(it.tag)?.let { activity.supportFragmentManager.beginTransaction().detach(it).commitNowAllowingStateLoss() } }
	}
}