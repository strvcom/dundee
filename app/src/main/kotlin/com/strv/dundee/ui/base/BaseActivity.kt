package com.strv.dundee.ui.base

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem


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
		supportActionBar?.setDisplayUseLogoEnabled(false)
		supportActionBar?.setDisplayShowTitleEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)
	}
}