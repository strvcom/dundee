package com.strv.dundee.firestore

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/*
User object

Notes:
- data class needs to have default value so that it has empty constructor ready for Firestore
- properties also need to be vars because Firestore needs setters
 */
data class User(
		var name: String? = null,
		var born: Int? = null,
		var isAdult: Boolean? = null,
		var timestamp: Date? = null,
		@ServerTimestamp var timestampAuto: Date? = null
)