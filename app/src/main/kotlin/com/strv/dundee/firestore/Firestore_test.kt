package com.strv.dundee.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.strv.ktools.logD
import com.strv.ktools.logMeD


object Firestore {
	@SuppressLint("StaticFieldLeak")
	val db = FirebaseFirestore.getInstance()

	fun read() {

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

	fun set(collection: String, data: HashMap<String, Any?>, documentId: String? = null, merge: Boolean = false) {
		val documentRef = if (documentId != null) db.collection(collection).document(documentId) else db.collection(collection).document()
		val docId = documentRef.id
		val task = if(merge) documentRef.set(data, SetOptions.merge()) else documentRef.set(data)
		task.addOnSuccessListener { logD("Success, collection $collection documentID = $docId") }
		task.addOnFailureListener { e -> e.logMeD() }
	}

	fun set(collection: String, data: Any, documentId: String? = null, merge: Boolean = false) {
		val documentRef = if (documentId != null) db.collection(collection).document(documentId) else db.collection(collection).document()
		val docId = documentRef.id
		val task = if(merge) documentRef.set(data, SetOptions.merge()) else documentRef.set(data)
		task.addOnSuccessListener { logD("Set success, collection $collection documentID = $docId") }
		task.addOnFailureListener { e -> e.logMeD() }
	}

	fun updateField(collection: String, documentId: String, field: String, value: Any?) {
		val documentRef = db.collection(collection).document(documentId)
		documentRef.update(field, value)
				.addOnSuccessListener { logD("Update field success, collection $collection documentId = $documentId") }
				.addOnFailureListener { e -> e.logMeD() }
	}

	fun deleteDocument(collection: String, documentId: String) {
		val documentRef = db.collection(collection).document(documentId)
		documentRef.delete()
				.addOnSuccessListener { logD("Delete success, collection $collection documentId = $documentId") }
				.addOnFailureListener { e -> e.logMeD() }
	}

	fun deleteField(collection: String, documentId: String, field: String) {
		val documentRef = db.collection(collection).document(documentId)
		val update = HashMap<String, Any>()
		update.put(field, FieldValue.delete())
		documentRef.update(update)
				.addOnSuccessListener { logD("Delete field success, collection $collection documentId = $documentId") }
				.addOnFailureListener { e -> e.logMeD() }
	}
}