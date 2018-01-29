package com.strv.dundee.model.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import java.util.Date

class Converters {
	@TypeConverter
	fun fromTimestamp(value: Long?): Date? {
		return if (value == null) null else Date(value)
	}

	@TypeConverter
	fun dateToTimestamp(date: Date?): Long? {
		return date?.time
	}

	@TypeConverter
	fun mapToString(map: Map<String, Double>?): String? {
		return if (map == null) null else Gson().toJson(map)
	}

	@TypeConverter
	fun stringToMap(json: String?): Map<String, Double>? {
		return Gson().fromJson(json, HashMap<String, Double>().javaClass)
	}
}