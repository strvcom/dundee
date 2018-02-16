package com.strv.dundee.model.entity

import android.app.Application
import android.support.annotation.StringRes
import com.strv.dundee.R
import com.strv.ktools.inject

enum class TimeFrame(@StringRes private val titleResId: Int, val key: String) {
	DAY(R.string.time_frame_day, "1day"),
	WEEK(R.string.time_frame_week, "7day"),
	MONTH(R.string.time_frame_month, "30day"),
	QUARTER(R.string.time_frame_quarter, "90day"),
	HALF(R.string.time_frame_half, "180day"),
	YEAR(R.string.time_frame_year, "365day"),
	ALL(R.string.time_frame_all, "all");

	val application by inject<Application>()

	override fun toString(): String {
		return application.getString(titleResId)
	}

	companion object {
		fun fromString(value: String?) = value?.let { TimeFrame.values().find { it.key == value } ?: ALL } ?: ALL
	}
}