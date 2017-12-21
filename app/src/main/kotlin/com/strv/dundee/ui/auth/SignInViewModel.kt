package com.strv.dundee.ui.auth

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.strv.ktools.SingleLiveData
import com.strv.ktools.logD


class SignInViewModel() : ViewModel() {

	data class SignInResult(val success: Boolean, val errorMessage: String? = null)

	val result = SingleLiveData<SignInResult>()
	val email = ObservableField<String>()
	val password = ObservableField<String>()
	val formValid = ObservableBoolean(false)
	val progress = ObservableBoolean(false)

	fun checkInput() {
		formValid.set(!(email.get() == null || email.get()!!.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.get()).matches() || password.get() == null || password.get()!!.isEmpty() || password.get()!!.length < 6))
	}

	fun signIn() {
		progress.set(true)
		FirebaseAuth.getInstance().signInWithEmailAndPassword(email.get()!!, password.get()!!)
				.addOnCompleteListener {
					progress.set(false)
					if (it.isSuccessful) {
						logD("Sign In successful")
						result.value = SignInResult(true)
					} else {
						logD("Sign In error: ${it.exception?.message}")
						result.value = SignInResult(false, it.exception?.message)
					}
				}
	}
}