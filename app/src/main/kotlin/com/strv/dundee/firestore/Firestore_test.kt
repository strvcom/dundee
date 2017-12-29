package com.strv.dundee.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.strv.ktools.logD
import com.strv.ktools.logMeD


object Firestore {
	@SuppressLint("StaticFieldLeak")
	val db = FirebaseFirestore.getInstance()

	fun observeDocuments(collection: String, where: Pair<String, Any>? = null): ListenerRegistration {
		val collectionRef = db.collection(collection)
		val query = if(where != null) collectionRef.whereEqualTo(where.first, where.second) else collectionRef
		return query.addSnapshotListener { value, e ->
			if (e != null) {
				logD("Listen failed.", e)
				return@addSnapshotListener
			}

			val documents = value.documents // whole list every time
			val changes = value.documentChanges // whole list just for the first time, then only changes, deletions, etc
			for(doc in documents) {
				doc.data.logMeD()
				doc.metadata.hasPendingWrites().logMeD()
			}
			for(doc in changes) {
				doc.document.data.logMeD()
				doc.type.logMeD()
			}
		}
	}

	fun getDocuments(collection: String, where: Pair<String, Any>? = null) {
		val collectionRef = db.collection(collection)
		val query = if(where != null) collectionRef.whereEqualTo(where.first, where.second) else collectionRef
		query.get().addOnCompleteListener { task ->
			if (task.isSuccessful) {
				for (document in task.result) {
					logD("${document.id} => ${document.data}")
				}
			} else {
				logD("Error getting documents ${task.exception}")
			}
		}
	}

	fun <T : Any?> getDocuments(collection: String, clazz: Class<T>, where: Pair<String, Any>? = null) {
		val collectionRef = db.collection(collection)
		val query = if(where != null) collectionRef.whereEqualTo(where.first, where.second) else collectionRef
		query.get().addOnCompleteListener { task ->
			if (task.isSuccessful) {
				val list = task.result.toObjects(clazz)
				for (item in list) {
					item.logMeD()
				}
			} else {
				logD("Error getting documents ${task.exception}")
			}
		}
	}

	fun getDocument(collection: String, documentId: String) {
		val documentRef = db.collection(collection).document(documentId)
		documentRef.get().addOnCompleteListener { task ->
			if(task.isSuccessful) {
				val documentSnapshot = task.result
				if(documentSnapshot != null) documentSnapshot.data.logMeD()
				else logD("No document with this id")
			} else {
				logD("Error getting document ${task.exception}")
			}
		}
	}

	fun <T : Any?> getDocument(collection: String, documentId: String, clazz: Class<T>) {
		val documentRef = db.collection(collection).document(documentId)
		documentRef.get().addOnCompleteListener { task ->
			if(task.isSuccessful) {
				val documentSnapshot = task.result
				if(documentSnapshot != null) documentSnapshot.toObject(clazz).logMeD()
				else logD("No document with this id")
			} else {
				logD("Error getting document ${task.exception}")
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