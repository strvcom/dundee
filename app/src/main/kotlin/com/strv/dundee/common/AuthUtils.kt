package com.strv.dundee.common

import com.google.firebase.auth.FirebaseAuth

fun isUserSignedIn() = FirebaseAuth.getInstance().currentUser != null