package com.strv.ktools

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferencesProvider.liveDataDelegate(
	defaultValue: T? = null,
	key: String? = null,
	crossinline getter: SharedPreferences.(String, T?) -> T?,
	crossinline setter: Editor.(String, T?) -> Editor
): ReadOnlyProperty<Any?, MutableLiveData<T?>> = object : MutableLiveData<T?>(), ReadOnlyProperty<Any?, MutableLiveData<T?>>, SharedPreferences.OnSharedPreferenceChangeListener {
	var originalProperty: KProperty<*>? = null
	lateinit var prefKey: String

	override fun getValue(thisRef: Any?, property: KProperty<*>): MutableLiveData<T?> {
		originalProperty = property
		prefKey = key ?: originalProperty!!.name
		return this
	}

	override fun getValue(): T? {

		val value = provide().getter(prefKey, defaultValue)
		return super.getValue()
			?: value
			?: defaultValue
	}

	override fun setValue(value: T?) {
		super.setValue(value)
		provide().edit().setter(prefKey, value).apply()
	}

	override fun onActive() {
		super.onActive()
		value = provide().getter(prefKey, defaultValue)
		provide().registerOnSharedPreferenceChangeListener(this)
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, changedKey: String) {
		if (changedKey == prefKey) {
			value = sharedPreferences.getter(changedKey, defaultValue)
		}
	}

	override fun onInactive() {
		super.onInactive()
		provide().unregisterOnSharedPreferenceChangeListener(this)
	}
}

private inline fun <T> SharedPreferencesProvider.liveDataDelegatePrimitive(
	defaultValue: T,
	key: String? = null,
	crossinline getter: SharedPreferences.(String, T) -> T,
	crossinline setter: Editor.(String, T) -> Editor
): ReadOnlyProperty<Any?, MutableLiveData<T>> = object : MutableLiveData<T>(), ReadOnlyProperty<Any?, MutableLiveData<T>>, SharedPreferences.OnSharedPreferenceChangeListener {
	var originalProperty: KProperty<*>? = null
	lateinit var prefKey: String

	override fun getValue(thisRef: Any?, property: KProperty<*>): MutableLiveData<T> {
		originalProperty = property
		prefKey = key ?: originalProperty!!.name
		return this
	}

	override fun getValue(): T {

		val value = provide().getter(prefKey, defaultValue)
		return super.getValue()
			?: value
			?: defaultValue
	}

	override fun setValue(value: T) {
		super.setValue(value)
		provide().edit().setter(prefKey, value).apply()
	}

	override fun onActive() {
		super.onActive()
		value = provide().getter(prefKey, defaultValue)
		provide().registerOnSharedPreferenceChangeListener(this)
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, changedKey: String) {
		if (changedKey == prefKey) {
			value = sharedPreferences.getter(changedKey, defaultValue)
		}
	}

	override fun onInactive() {
		super.onInactive()
		provide().unregisterOnSharedPreferenceChangeListener(this)
	}
}

fun SharedPreferencesProvider.intLiveData(def: Int, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<Int>> = liveDataDelegatePrimitive(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)
fun SharedPreferencesProvider.longLiveData(def: Long, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<Long>> = liveDataDelegatePrimitive(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
fun SharedPreferencesProvider.floatLiveData(def: Float, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<Float>> = liveDataDelegatePrimitive(def, key, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
fun SharedPreferencesProvider.booleanLiveData(def: Boolean, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<Boolean>> = liveDataDelegatePrimitive(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
fun SharedPreferencesProvider.stringLiveData(def: String? = null, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<String?>> = liveDataDelegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)
fun SharedPreferencesProvider.stringSetLiveData(def: Set<String>? = null, key: String? = null): ReadOnlyProperty<Any?, MutableLiveData<Set<String>?>> = liveDataDelegate(def, key, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet)

class SharedPreferencesProvider(val provider: () -> SharedPreferences) {
	fun provide() = provider()
}

private fun sharedPrefs(context: Context, name: String?, mode: Int) = SharedPreferencesProvider({
	if (name == null)
		PreferenceManager.getDefaultSharedPreferences(context)
	else
		context.getSharedPreferences(name, mode)
})

fun AndroidViewModel.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(getApplication(), name, mode)
fun Activity.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
fun Fragment.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(activity, name, mode)
fun Application.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
fun Service.sharedPrefs(name: String? = null, mode: Int = ContextWrapper.MODE_PRIVATE) = sharedPrefs(this, name, mode)
