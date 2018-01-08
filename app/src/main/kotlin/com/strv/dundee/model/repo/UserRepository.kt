package com.strv.dundee.model.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.model.entity.User
import com.strv.ktools.FirestoreDocumentQueryLiveData
import com.strv.ktools.inject

class UserRepository {
	private val firestore by inject<FirebaseFirestore>()
	private val auth by inject<FirebaseAuth>()

	private val userCollection = firestore.collection(User.COLLECTION)

	val currentUser: FirebaseUser?
		get() = auth.currentUser

	fun getCurrentUserData() = FirestoreDocumentQueryLiveData(userCollection.whereEqualTo("uid", auth.currentUser?.uid), User::class.java)

	fun signOut() {
		auth.signOut()
	}

	fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
		return auth.createUserWithEmailAndPassword(email, password)
	}

	fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
		return auth.signInWithEmailAndPassword(email, password)
	}

	fun signInWithGoogle(accountIdToken: String): Task<AuthResult> {
		val credential = GoogleAuthProvider.getCredential(accountIdToken, null)
		return auth.signInWithCredential(credential)
	}
}
