package com.strv.dundee.ui.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.strv.dundee.app.Config
import com.strv.ktools.EventLiveData
import com.strv.ktools.inject
import com.strv.ktools.logD

class SignUpViewModel(val defaultEmail: String? = null, val defaultPassword: String? = null) : ViewModel() {

	data class SignUpResult(val success: Boolean, val errorMessage: String? = null)

	val config by inject<Config>()
	val result = EventLiveData<SignUpResult>()
	val email = MutableLiveData<String>().apply { value = defaultEmail }
	val password = MutableLiveData<String>().apply { value = defaultPassword }
	val formValid = MutableLiveData<Boolean>().apply { value = false }
	val progress = MutableLiveData<Boolean>().apply { value = false }

	fun checkInput() {
		formValid.value = !(email.value == null || email.value!!.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.value).matches() || password.value == null || password.value!!.isEmpty() || password.value!!.length < config.MIN_PASSWORD_LENGTH)
	}

	fun createAccount() {
		progress.value = true
		FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value!!, password.value!!)
				.addOnCompleteListener {
					progress.value = false
					if (it.isSuccessful) {
						logD("Sign Up successful")
						result.publish(SignUpResult(true))
					} else {
						logD("Sign Up error: ${it.exception?.message}")
						result.publish(SignUpResult(false, it.exception?.message))
					}
				}
	}
}