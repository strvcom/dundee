package com.strv.dundee.model.entity

import com.google.firebase.firestore.Exclude


open class Document(@get:Exclude var docId: String? = null)