package com.strv.dundee.ui.base

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup

abstract class BaseActivity : AppCompatActivity() {

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return when (item?.itemId) {
			android.R.id.home -> {
				onBackPressed()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	protected fun setupToolbar(toolbar: Toolbar) {
		setSupportActionBar(toolbar)
		supportActionBar?.apply {
			setDisplayUseLogoEnabled(false)
			setDisplayShowTitleEnabled(true)
			setDisplayShowHomeEnabled(true)
			setDisplayHomeAsUpEnabled(true)
			setHomeButtonEnabled(true)
		}
	}

	protected fun setToolbarTitle(@StringRes titleResId: Int) {
		supportActionBar?.title = getString(titleResId)
	}

	protected fun setToolbarTitle(title: String) {
		supportActionBar?.title = title
	}

	protected fun showSnackbar(message: String) {
		val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
		Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
	}
}