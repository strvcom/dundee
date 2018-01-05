package com.strv.dundee.model.firestore

import android.arch.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.strv.dundee.model.repo.common.Resource
import com.strv.dundee.model.repo.common.Status
import com.strv.ktools.logD


class FirestoreDocumentLiveData<T>(private val documentRef: DocumentReference, private val clazz: Class<T>) : LiveData<Resource<T>>() {

	// if cached data are up to date with server DB, listener won't get called again with isFromCache=false
	private val listener = EventListener<DocumentSnapshot> { value, e ->
		logD("Loaded ${documentRef.path}, cache: ${value?.metadata?.isFromCache.toString()} error: ${e?.message}")
		setValue(Resource(if (e != null) Status.ERROR else if (value.metadata.isFromCache) Status.LOADING else Status.SUCCESS,
				value?.toObject(clazz), e?.message))
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