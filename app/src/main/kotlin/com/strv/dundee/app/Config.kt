package com.strv.dundee.app

import com.strv.dundee.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

object Config {
	const val LOG_TAG = "dundee"
	val HTTP_LOGGING_LEVEL = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
	const val MIN_PASSWORD_LENGTH = 6
	const val EXCHANGE_RATE_TTL_DAYS = 1
}