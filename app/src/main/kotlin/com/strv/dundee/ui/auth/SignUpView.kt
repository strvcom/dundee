package com.strv.dundee.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivitySignUpBinding
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.vmb

interface SignUpView {
}

class SignUpActivity : BaseActivity(), SignUpView {

	companion object {
		fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
	}

	private val vmb by vmb<SignUpViewModel, ActivitySignUpBinding>(R.layout.activity_sign_up) { SignUpViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)

		vmb.viewModel.result.observe(this, Observer { result ->
			result?.let {
				if (it.success) {
					setResult(Activity.RESULT_OK)
					finish()
				} else {
					Toast.makeText(this, it.errorMessage, Toast.LENGTH_LONG).show()
				}
			}
		})
	}
}