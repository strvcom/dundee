package com.strv.dundee.ui.auth

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Patterns
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.strv.dundee.R
import com.strv.dundee.app.Config
import com.strv.ktools.SingleLiveData
import com.strv.ktools.inject
import com.strv.ktools.logD


class SignInViewModel() : ViewModel() {

	data class SignInResult(val success: Boolean, val errorMessage: String? = null)

	val application by inject<Application>()
	val config by inject<Config>()
	val result = SingleLiveData<SignInResult>()
	val email = ObservableField<String>()
	val password = ObservableField<String>()
	val formValid = ObservableBoolean(false)
	val progress = ObservableBoolean(false)

	val googleSignInRequest = SingleLiveData<Intent>()


	fun checkInput() {
		formValid.set(!(email.get() == null || email.get()!!.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.get()).matches() || password.get() == null || password.get()!!.isEmpty() || password.get()!!.length < config.MIN_PASSWORD_LENGTH))
	}

	fun signIn() {
		progress.set(true)
		FirebaseAuth.getInstance().signInWithEmailAndPassword(email.get()!!, password.get()!!)
				.addOnSuccessListener {
					logD("Sign In successful")
					result.value = SignInResult(true)
				}
				.addOnFailureListener { exception ->
					result.value = SignInResult(false, exception.message)
				}
				.addOnCompleteListener {
					progress.set(false)
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
			progress.set(true)

			val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
			FirebaseAuth.getInstance().signInWithCredential(credential)
					.addOnSuccessListener {
						val user = FirebaseAuth.getInstance().getCurrentUser()
						result.value = SignInResult(true)
					}
					.addOnFailureListener { exception ->
						result.value = SignInResult(false, exception.message)
					}
					.addOnCompleteListener {
						progress.set(false)
					}

		}.addOnFailureListener { exception ->
			result.value = SignInResult(false, exception.message)
		}


	}
}