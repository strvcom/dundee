package com.strv.ktools

import android.util.Log

private var logTag = "Log"

fun setLogTag(tag: String) {
    logTag = tag
}

fun log(message: String, vararg args: Any?) = Log.d(logTag, message.format(args))
fun logD(message: String, vararg args: Any?) = Log.d(logTag, message.format(args))
fun logE(message: String, vararg args: Any?) = Log.e(logTag, message.format(args))
fun logI(message: String, vararg args: Any?) = Log.i(logTag, message.format(args))
fun logW(message: String, vararg args: Any?) = Log.w(logTag, message.format(args))

fun Any?.logMe() = logD(this.toString())
fun Any?.logMeD() = logD(this.toString())
fun Any?.logMeI() = logI(this.toString())