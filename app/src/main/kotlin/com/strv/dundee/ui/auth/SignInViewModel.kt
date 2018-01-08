package com.strv.dundee.ui.auth

import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.strv.dundee.R
import com.strv.dundee.app.Config
import com.strv.dundee.model.repo.UserRepository
import com.strv.dundee.model.validateEmail
import com.strv.dundee.model.validatePassword
import com.strv.ktools.EventLiveData
import com.strv.ktools.addValueSource
import com.strv.ktools.inject
import com.strv.ktools.logD


class SignInViewModel() : ViewModel() {

	data class SignInResult(val success: Boolean, val errorMessage: String? = null)

	private val application by inject<Application>()
	private val config by inject<Config>()
	private val userRepository by inject<UserRepository>()

	val result = EventLiveData<SignInResult>()
	val googleSignInRequest = EventLiveData<Intent>()

	val email = MutableLiveData<String>()
	val password = MutableLiveData<String>()
	val formValid = MediatorLiveData<Boolean>()
			.addValueSource(email, { validateForm() })
			.addValueSource(password, { validateForm() })
	val progress = MutableLiveData<Boolean>().apply { value = false }


	private fun validateForm() = validateEmail(email.value) && validatePassword(password.value, config.MIN_PASSWORD_LENGTH)

	fun signIn() {
		progress.value = true
		userRepository.signInWithEmailAndPassword(email.value!!, password.value!!)
				.addOnSuccessListener {
					logD("Sign In successful")
					result.publish(SignInResult(true))
				}
				.addOnFailureListener { exception ->
					result.publish(SignInResult(false, exception.message))
				}
				.addOnCompleteListener {
					progress.value = false
				}
	}

	fun signInWithGoogle() {
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(application.getString(R.string.default_web_client_id))
				.requestEmail()
				.build()
		val signInIntent = GoogleSignIn.getClient(application, gso).signInIntent
		googleSignInRequest.value = signInIntent
	}

	fun onGoogleSignInResult(data: Intent) {
		GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener { account ->
			progress.value = true
			userRepository.signInWithGoogle(account.idToken!!)
					.addOnSuccessListener {
						val user = userRepository.getCurrentUserData()
						result.publish(SignInResult(true))
					}
					.addOnFailureListener { exception ->
						result.publish(SignInResult(false, exception.message))
					}
					.addOnCompleteListener {
						progress.value = false
					}

		}.addOnFailureListener { exception ->
			result.publish(SignInResult(false, exception.message))
		}

	}
}