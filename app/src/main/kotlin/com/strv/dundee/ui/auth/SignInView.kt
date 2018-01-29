package com.strv.dundee.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivitySignInBinding
import com.strv.dundee.ui.base.BaseActivity
import com.strv.dundee.ui.main.MainActivity
import com.strv.ktools.vmb

interface SignInView {
	fun openSignUp()
	fun openMainActivity()
	fun openGoogleSignInActivity(signInIntent: Intent)
}

class SignInActivity : BaseActivity(), SignInView {
	companion object {
		private const val ACTION_SIGN_UP = 1
		private const val ACTION_SIGN_IN_GOOGLE = 2

		fun newIntent(context: Context) = Intent(context, SignInActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<SignInViewModel, ActivitySignInBinding>(R.layout.activity_sign_in) { SignInViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.result.observe(this, Observer { result ->
			result?.let {
				if (it.success)
					openMainActivity()
				else
					showSnackbar(it.errorMessage ?: getString(R.string.error_unknown))
			}
		})

		vmb.viewModel.googleSignInRequest.observe(this, Observer { openGoogleSignInActivity(it!!) })
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		when (requestCode) {
			ACTION_SIGN_UP -> if (resultCode == Activity.RESULT_OK) openMainActivity()
			ACTION_SIGN_IN_GOOGLE -> data?.let { vmb.viewModel.onGoogleSignInResult(data) }
		}
	}

	override fun openSignUp() {
		startActivityForResult(SignUpActivity.newIntent(this, vmb.viewModel.email.value, vmb.viewModel.password.value), ACTION_SIGN_UP)
	}

	override fun openMainActivity() {
		startActivity(MainActivity.newIntent(this))
		finish()
	}

	override fun openGoogleSignInActivity(signInIntent: Intent) {
		startActivityForResult(signInIntent, ACTION_SIGN_IN_GOOGLE)
	}
}