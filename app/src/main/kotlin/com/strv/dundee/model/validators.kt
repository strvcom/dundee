package com.strv.dundee.model

import android.util.Patterns

fun validateEmail(email: String?) = email != null && email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
fun validatePassword(password: String?, minLength: Int) = password != null && password.isNotEmpty() && password.length >= minLength
