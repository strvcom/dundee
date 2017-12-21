package com.strv.dundee.ui.main

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

		if(!isUserSignedIn()) {
			startActivity(SignInActivity.newIntent(this))
			finish()
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
		}
		return super.onOptionsItemSelected(item)
	}
}
