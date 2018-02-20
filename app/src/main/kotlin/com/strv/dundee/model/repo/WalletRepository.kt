package com.strv.dundee.model.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.strv.dundee.model.entity.Wallet
import com.strv.ktools.FirestoreDocumentListLiveData
import com.strv.ktools.inject

class WalletRepository {
	private val firestore by inject<FirebaseFirestore>()
	private val auth by inject<FirebaseAuth>()

	private val walletCollection = firestore.collection(Wallet.COLLECTION)

	fun getWalletsForCurrentUser(coin: String? = null): FirestoreDocumentListLiveData<Wallet> {
		var query = walletCollection.whereEqualTo("uid", auth.currentUser?.uid).orderBy(Wallet.ATTR_BOUGHT_DATE, Query.Direction.DESCENDING)
		if (coin != null) query = query.whereEqualTo(Wallet.ATTR_COIN, coin)
		return FirestoreDocumentListLiveData(query, Wallet::class.java)
	}

	fun addWalletToCurrentUser(wallet: Wallet): Task<DocumentReference> {
		wallet.uid = auth.currentUser!!.uid
		return walletCollection.add(wallet)
	}

	fun updateWallet(wallet: Wallet): Task<Void> {
		if (wallet.docId != null) {
			return walletCollection.document(wallet.docId!!).set(wallet)
		} else {
			throw (IllegalArgumentException("Unable to update document. It's document ID is null."))
		}
	}

	fun removeWallet(wallet: Wallet): Task<Void> {
		if (wallet.docId != null) {
			return walletCollection.document(wallet.docId!!).delete()
		} else {
			throw (IllegalArgumentException("Unable to remove document. It's document ID is null."))
		}
	}
}
