package com.strv.dundee.ui.main

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.strv.dundee.R
import com.strv.dundee.common.isUserSignedIn
import com.strv.dundee.databinding.ActivityMainBinding
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.ui.auth.SignInActivity
import com.strv.ktools.LifecycleAwareBindingRecyclerViewAdapter
import com.strv.ktools.vmb

interface MainView {
	val lifecycleAwareAdapter: LifecycleAwareBindingRecyclerViewAdapter<Wallet> // TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
	fun addAmount()
}

class MainActivity : AppCompatActivity(), MainView {
	companion object {
		private const val ACTION_ADD_AMOUNT = 1

		fun newIntent(context: Context) = Intent(context, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main) { MainViewModel() }
	override val lifecycleAwareAdapter = LifecycleAwareBindingRecyclerViewAdapter<Wallet>(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setSupportActionBar(vmb.binding.toolbar)

		if (!isUserSignedIn()) {
			startActivity(SignInActivity.newIntent(this))
			finish()
		}

		vmb.viewModel.walletRemovedSnackBar.observe(this, Observer { wallet ->
			val snackbar = Snackbar.make(vmb.rootView, R.string.item_removed, Snackbar.LENGTH_SHORT)
			snackbar.setAction(R.string.undo, { wallet?.let { vmb.viewModel.addWallet(it) } })
			snackbar.show()
		})
		vmb.viewModel.walletOpened.observe(this, Observer { it?.let {startActivity(AddAmountActivity.newIntent(this, it))} })
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


	override fun addAmount() {
		startActivityForResult(AddAmountActivity.newIntent(this), ACTION_ADD_AMOUNT)
	}
}
