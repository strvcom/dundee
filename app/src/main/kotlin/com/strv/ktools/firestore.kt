package com.strv.ktools

import android.arch.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.strv.dundee.model.entity.Document

class FirestoreDocumentLiveData<T>(private val documentRef: DocumentReference, private val clazz: Class<T>) : LiveData<Resource<T>>() {

	// if cached data are up to date with server DB, listener won't get called again with isFromCache=false
	private val listener = EventListener<DocumentSnapshot> { value, e ->
		logD("Loaded ${documentRef.path}, cache: ${value?.metadata?.isFromCache.toString()} error: ${e?.message}")
		val document = value?.toObject(clazz)
		if (document != null && document is Document) {
			document.docId = value.id
		}
		setValue(Resource(if (e != null) Resource.Status.ERROR else if (value.metadata.isFromCache) Resource.Status.LOADING else Resource.Status.SUCCESS,
			document, e?.message))
	}
	private lateinit var listenerRegistration: ListenerRegistration

	override fun onActive() {
		super.onActive()
		logD("Start listening ${documentRef.path}")
		listenerRegistration = documentRef.addSnapshotListener(listener)
	}

	override fun onInactive() {
		super.onInactive()
		logD("Stop listening ${documentRef.path}")
		listenerRegistration.remove()
	}
}

class FirestoreDocumentQueryLiveData<T>(private val query: Query, private val clazz: Class<T>) : LiveData<Resource<T>>() {

	// if cached data are up to date with server DB, listener won't get called again with isFromCache=false
	private val listener = EventListener<QuerySnapshot> { value, e ->
		logD("Loaded $query, cache: ${value?.metadata?.isFromCache.toString()} error: ${e?.message}")
		val list: ArrayList<T> = ArrayList()
		value?.documents?.mapTo(list) {
			val item = it.toObject(clazz)
			if (item is Document) {
				(item as Document).docId = it.id
			}
			item
		}
		setValue(Resource(if (e != null) Resource.Status.ERROR else if (value.metadata.isFromCache) Resource.Status.LOADING else Resource.Status.SUCCESS,
			if (!list.isEmpty()) list[0] else null, e?.message))
	}
	private lateinit var listenerRegistration: ListenerRegistration

	override fun onActive() {
		super.onActive()
		logD("Start listening $query")
		listenerRegistration = query.limit(1).addSnapshotListener(listener)
	}

	override fun onInactive() {
		super.onInactive()
		logD("Stop listening $query")
		listenerRegistration.remove()
	}
}

class FirestoreDocumentListLiveData<T>(private val query: Query, private val clazz: Class<T>) : LiveData<Resource<List<T>>>() {

	// if cached data are up to date with server DB, listener won't get called again with isFromCache=false
	private val listener = EventListener<QuerySnapshot> { value, e ->
		logD("Loaded $query, cache: ${value?.metadata?.isFromCache.toString()} error: ${e?.message}")
		val list: ArrayList<T> = ArrayList()
		value?.documents?.mapTo(list) {
			val item = it.toObject(clazz)
			if (item is Document) {
				(item as Document).docId = it.id
			}
			item
		}
		setValue(Resource(if (e != null) Resource.Status.ERROR else if (value.metadata.isFromCache) Resource.Status.LOADING else Resource.Status.SUCCESS, list, e?.message))
	}
	private lateinit var listenerRegistration: ListenerRegistration

	override fun onActive() {
		super.onActive()
		logD("Start listening $query")
		listenerRegistration = query.addSnapshotListener(listener)
	}

	override fun onInactive() {
		super.onInactive()
		logD("Stop listening $query")
		listenerRegistration.remove()
	}
}