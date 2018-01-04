package com.strv.dundee.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivitySignInBinding
import com.strv.dundee.ui.main.MainActivity
import com.strv.ktools.vmb


interface SignInView {
	fun openSignUp()
}

class SignInActivity : AppCompatActivity(), SignInView {

	private val ACTION_SIGN_UP = 1
	private val ACTION_SIGN_IN_GOOGLE = 2

	companion object {
		fun newIntent(context: Context) = Intent(context, SignInActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<SignInViewModel, ActivitySignInBinding>(R.layout.activity_sign_in) { SignInViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		vmb.viewModel.result.observe(this, Observer { result ->
			result?.let {
				if (it.success) startMainActivity()
				else Toast.makeText(this, it.errorMessage, Toast.LENGTH_LONG).show()
			}
		})

		vmb.viewModel.googleSignInRequest.observe(this, Observer { openGoogleSignInActivity(it!!) })
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		when (requestCode) {
			ACTION_SIGN_UP ->
				if (resultCode == Activity.RESULT_OK) startMainActivity()
			ACTION_SIGN_IN_GOOGLE -> {
				data?.let { vmb.viewModel.onGoogleSignInResult(data) }
			}
		}
	}

	override fun openSignUp() {
		startActivityForResult(SignUpActivity.newIntent(this, vmb.viewModel.email.value, vmb.viewModel.password.value), ACTION_SIGN_UP)
	}

	private fun startMainActivity() {
		startActivity(MainActivity.newIntent(this))
		finish()
	}

	private fun openGoogleSignInActivity(signInIntent: Intent) {
		startActivityForResult(signInIntent, ACTION_SIGN_IN_GOOGLE)
	}
}