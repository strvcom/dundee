package com.strv.dundee.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivitySignUpBinding
import com.strv.dundee.ui.base.BaseActivity
import com.strv.ktools.vmb

interface SignUpView {
}

class SignUpActivity : BaseActivity(), SignUpView {

	companion object {
		const val EXTRA_DEFAULT_EMAIL = "default_email"
		const val EXTRA_DEFAULT_PASSWORD = "default_password"

		fun newIntent(context: Context, defaultEmail: String? = null, defaultPassword: String? = null) = Intent(context, SignUpActivity::class.java).apply {
			putExtra(EXTRA_DEFAULT_EMAIL, defaultEmail)
			putExtra(EXTRA_DEFAULT_PASSWORD, defaultPassword)
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		}
	}

	private val vmb by vmb<SignUpViewModel, ActivitySignUpBinding>(R.layout.activity_sign_up) { SignUpViewModel(intent.getStringExtra(EXTRA_DEFAULT_EMAIL), intent.getStringExtra(EXTRA_DEFAULT_PASSWORD)) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupToolbar(vmb.binding.toolbar)

		vmb.viewModel.result.observe(this, Observer { result ->
			result?.let {
				if (it.success) {
					setResult(Activity.RESULT_OK)
					finish()
				} else
					Snackbar.make(vmb.rootView, it.errorMessage ?: getString(com.strv.dundee.R.string.error_unknown), Snackbar.LENGTH_SHORT).show()
			}
		})
	}
}