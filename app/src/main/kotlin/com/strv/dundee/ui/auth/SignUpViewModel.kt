package com.strv.dundee.ui.auth

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.strv.dundee.app.Config
import com.strv.dundee.model.validateEmail
import com.strv.dundee.model.validatePassword
import com.strv.ktools.EventLiveData
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.logD

class SignUpViewModel(val defaultEmail: String? = null, val defaultPassword: String? = null) : ViewModel() {

	data class SignUpResult(val success: Boolean, val errorMessage: String? = null)

	val config by inject<Config>()
	val result = EventLiveData<SignUpResult>()
	val email = MutableLiveData<String>().apply { value = defaultEmail }
	val password = MutableLiveData<String>().apply { value = defaultPassword }
	val formValid = MediatorLiveData<Boolean>()
			.addValueSource(email, { validateForm() })
			.addValueSource(password, { validateForm() })
	val progress = MutableLiveData<Boolean>().apply { value = false }


	fun validateForm() = validateEmail(email.value) && validatePassword(password.value, config.MIN_PASSWORD_LENGTH)

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