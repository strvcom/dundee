package com.strv.dundee.ui.auth

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.strv.dundee.app.Config
import com.strv.ktools.SingleLiveData
import com.strv.ktools.inject
import com.strv.ktools.logD

class SignUpViewModel() : ViewModel() {

	data class SignUpResult(val success: Boolean, val errorMessage: String? = null)

	val config by inject<Config>()
	val result = SingleLiveData<SignUpResult>()
	val email = ObservableField<String>()
	val password = ObservableField<String>()
	val formValid = ObservableBoolean(false)
	val progress = ObservableBoolean(false)

	fun checkInput() {
		formValid.set(!(email.get() == null || email.get()!!.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.get()).matches() || password.get() == null || password.get()!!.isEmpty() || password.get()!!.length < config.MIN_PASSWORD_LENGTH))
	}

	fun createAccount() {
		progress.set(true)
		FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.get()!!, password.get()!!)
				.addOnCompleteListener {
					progress.set(false)
					if (it.isSuccessful) {
						logD("Sign Up successful")
						result.value = SignUpResult(true)
					} else {
						logD("Sign Up error: ${it.exception?.message}")
						result.value = SignUpResult(false, it.exception?.message)
					}
				}
	}
}