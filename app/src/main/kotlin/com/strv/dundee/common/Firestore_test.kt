package com.strv.dundee.common

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.strv.ktools.logD
import com.strv.ktools.logMeD


object Firestore {

	fun getUser(): HashMap<String, Any?> {
		val user = HashMap<String, Any?>()
		user.put("first", "Leos")
		user.put("last", "Dostal")
		user.put("born", 1990)
		return user
	}

	fun read() {

		val db = FirebaseFirestore.getInstance()

		db.collection("users")
				.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						for (document in task.result) {
							logD(document.id + " => " + document.data)
						}
					} else {
						logD("Error getting documents.", task.exception)
					}
				}
	}

	fun add() {
		val db = FirebaseFirestore.getInstance()

		val user = HashMap<String, Any>()
		user.put("first", "Leos")
		user.put("last", "Dostal")
		user.put("born", 1990)

		// Add a new document with a generated ID
		db.collection("users")
				.add(user)
				.addOnSuccessListener { documentReference -> logD("DocumentSnapshot added with ID: " + documentReference.id) }
				.addOnFailureListener { e -> logD("Error adding document", e) }
	}

	fun set(collection: String, documentId: String? = null, data: HashMap<String, Any?> = HashMap(), merge: Boolean = false) {
		val db = FirebaseFirestore.getInstance()
		val documentRef: DocumentReference
		documentRef = if (documentId != null) db.collection(collection).document(documentId) else db.collection(collection).document()
		val docId = documentRef.id
		val task = if(merge) documentRef.set(data, SetOptions.merge()) else documentRef.set(data)
		task.addOnSuccessListener { logD("Success, documentID = $docId") }
		task.addOnFailureListener { e -> e.logMeD() }
	}
}