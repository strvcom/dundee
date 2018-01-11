package com.strv.dundee.model.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.FirestoreDocumentListLiveData
import com.strv.ktools.inject

class WalletRepository {
	private val firestore by inject<FirebaseFirestore>()
	private val auth by inject<FirebaseAuth>()

	private val walletCollection = firestore.collection(Wallet.COLLECTION)

	fun getWalletsForCurrentUser() = FirestoreDocumentListLiveData(walletCollection.whereEqualTo("uid", auth.currentUser?.uid).orderBy("created"), Wallet::class.java)

	fun addWalletToCurrentUser(wallet: Wallet): Task<DocumentReference> {
		wallet.uid = auth.currentUser!!.uid
		return walletCollection.add(wallet)
	}

	fun removeWalletFromCurrentUser(wallet: Wallet): Task<Void> {
		if (wallet.docId != null) {
			return walletCollection.document(wallet.docId!!).delete()
		} else {
			throw (IllegalArgumentException("Unable to remove document. It's document ID is null."))
		}
	}
}
