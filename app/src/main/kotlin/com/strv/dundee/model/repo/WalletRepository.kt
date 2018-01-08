package com.strv.dundee.model.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.model.entity.Wallet
import com.strv.dundee.model.firestore.FirestoreDocumentsLiveData
import com.strv.ktools.inject

class WalletRepository {
	private val firestore by inject<FirebaseFirestore>()
	private val auth by inject<FirebaseAuth>()

	private val walletCollection = firestore.collection(Wallet.COLLECTION)

	fun getWalletsForCurrentUser() = FirestoreDocumentsLiveData(walletCollection.whereEqualTo("uid", auth.currentUser?.uid), Wallet::class.java)

	fun addWalletToCurrentUser(wallet: Wallet): Task<DocumentReference> {
		wallet.uid = auth.currentUser!!.uid
		return walletCollection.add(wallet)
	}
}
