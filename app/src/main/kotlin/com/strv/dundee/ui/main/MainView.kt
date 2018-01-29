package com.strv.dundee.ui.main

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.strv.dundee.R
import com.strv.dundee.common.isUserSignedIn
import com.strv.dundee.databinding.ActivityMainBinding
import com.strv.dundee.ui.auth.SignInActivity
import com.strv.dundee.ui.nav.MainNavigation
import com.strv.ktools.vmb

interface MainView {
}

class MainActivity : AppCompatActivity(), MainView {

	companion object {
		fun newIntent(context: Context) = Intent(context, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main) { MainViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setSupportActionBar(vmb.binding.toolbar)

		if (!isUserSignedIn()) {
			startActivity(SignInActivity.newIntent(this))
			finish()
		}

		setupNavigation(savedInstanceState)
	}

	private fun setupNavigation(savedInstanceState: Bundle?) {
		MainNavigation.Section.values().forEach { vmb.binding.bottomNavigationView.menu.add(Menu.NONE, it.ordinal, Menu.NONE, it.titleResId).apply { setIcon(it.iconResId) } }
		vmb.viewModel.navigationManager.currentTab.observe(this, Observer { it?.let { showFragment(it) } })

		if (savedInstanceState == null) {
			vmb.binding.bottomNavigationView.selectedItemId = MainNavigation.Section.DASHBOARD.ordinal
			vmb.viewModel.navigationManager.goToDashboard()
		}
		vmb.binding.bottomNavigationView.setOnNavigationItemSelectedListener {
			vmb.viewModel.navigationManager.goTo(MainNavigation.Section.values()[it.itemId])
			true
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.action_logout -> {
				vmb.viewModel.logout()
				startActivity(SignInActivity.newIntent(this))
				finish()
			}
			R.id.action_options -> vmb.viewModel.optionsOpen.value = vmb.viewModel.optionsOpen.value!!.not()
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onBackPressed() {
		if (vmb.viewModel.optionsOpen.value!!) {
			vmb.viewModel.optionsOpen.value = false
		} else {
			super.onBackPressed()
		}
	}

	private fun showFragment(section: MainNavigation.Section) {
		detachAllFragments()
		supportFragmentManager.beginTransaction().apply {
			var fragment = supportFragmentManager.findFragmentByTag(section.tag)
			if (fragment == null) {
				fragment = section.fragmentConstructor.invoke()
				add(R.id.main_container, fragment, section.tag)
			} else {
				attach(fragment)
			}
			commitAllowingStateLoss()
		}
	}

	private fun detachAllFragments() {
		MainNavigation.Section.values().forEach { supportFragmentManager.findFragmentByTag(it.tag)?.let { supportFragmentManager.beginTransaction().detach(it).commitNowAllowingStateLoss() } }
	}
}
