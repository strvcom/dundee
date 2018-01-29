package com.strv.dundee.model.entity

import com.google.firebase.auth.FirebaseAuth

/*
User object

Notes:
- data class needs to have default value so that it has empty constructor ready for Firestore
- properties also need to be vars because Firestore needs setters
 */
data class User(
	var uid: String? = FirebaseAuth.getInstance().currentUser?.uid,
	var email: String? = null
//		@ServerTimestamp var creationTime: Date? = null
) {
	companion object {
		const val COLLECTION = "users"
	}
}
