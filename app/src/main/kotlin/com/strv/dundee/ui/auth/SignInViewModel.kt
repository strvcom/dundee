package com.strv.dundee.ui.auth

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Patterns
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.strv.dundee.R
import com.strv.dundee.app.Config
import com.strv.ktools.EventLiveData
import com.strv.ktools.inject
import com.strv.ktools.logD


class SignInViewModel() : ViewModel() {

	data class SignInResult(val success: Boolean, val errorMessage: String? = null)

	val application by inject<Application>()
	val config by inject<Config>()
	val result = EventLiveData<SignInResult>()
	val email = MutableLiveData<String>()
	val password = MutableLiveData<String>()
	val formValid = MutableLiveData<Boolean>().apply { value = false }
	val progress = MutableLiveData<Boolean>().apply { value = false }

	val googleSignInRequest = EventLiveData<Intent>()


	fun checkInput() {
		formValid.value = !(email.value == null || email.value!!.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.value).matches() || password.value == null || password.value!!.isEmpty() || password.value!!.length < config.MIN_PASSWORD_LENGTH)
	}

	fun signIn() {
		progress.value = true
		FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value!!, password.value!!)
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
		val signInIntent = GoogleSignIn.getClient(application, gso).getSignInIntent()
		googleSignInRequest.value = signInIntent
	}

	fun onGoogleSignInResult(data: Intent) {
		GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener { account ->
			progress.value = true

			val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
			FirebaseAuth.getInstance().signInWithCredential(credential)
					.addOnSuccessListener {
						val user = FirebaseAuth.getInstance().getCurrentUser()
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