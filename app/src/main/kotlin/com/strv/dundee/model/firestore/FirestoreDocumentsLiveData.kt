package com.strv.dundee.model.firestore

import android.arch.lifecycle.LiveData
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.strv.dundee.model.repo.common.Resource
import com.strv.dundee.model.repo.common.Status
import com.strv.ktools.logD


class FirestoreDocumentsLiveData<T>(private val query: Query, private val clazz: Class<T>) : LiveData<Resource<List<T>>>() {

	// if cached data are up to date with server DB, listener won't get called again with isFromCache=false
	private val listener = EventListener<QuerySnapshot> { value, e ->
		logD("Loaded $query, cache: ${value?.metadata?.isFromCache.toString()} error: ${e?.message}")
		setValue(Resource(if (e != null) Status.ERROR else if (value.metadata.isFromCache) Status.LOADING else Status.SUCCESS,
				value?.toObjects(clazz), e?.message))
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