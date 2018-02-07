package com.strv.dundee.model.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.strv.dundee.model.entity.HistoryPrice
import com.strv.ktools.inject
import java.util.Date

class Converters {

	val gson by inject<Gson>()

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
		return map?.let { gson.toJson(it) }
	}

	@TypeConverter
	fun stringToMap(json: String?): Map<String, Double>? {
		return gson.fromJson(json, HashMap<String, Double>().javaClass)
	}

	@TypeConverter
	fun historyPriceListToString(list: List<HistoryPrice>?): String? {
		return list?.let { gson.toJson(it) }
	}

	@TypeConverter
	fun stringToHistoryPriceList(json: String?): List<HistoryPrice>? {
		return gson.fromJson(json, object : TypeToken<List<HistoryPrice>>() {}.type)
	}
}