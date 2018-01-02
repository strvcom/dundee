package com.strv.ktools

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.databinding.ObservableField
import android.preference.PreferenceManager
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferencesProvider.delegate(
		defaultValue: T?,
		key: String? = null,
		crossinline getter: SharedPreferences.(String, T?) -> T?,
		crossinline setter: Editor.(String, T?) -> Editor
) =
		object : ReadWriteProperty<Any?, T?> {
			override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
					provide().getter(key ?: property.name, defaultValue)

			override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) =
					provide().edit().setter(key ?: property.name, value).apply()
		}

private inline fun <T> SharedPreferencesProvider.delegatePrimitive(
		defaultValue: T,
		key: String? = null,
		crossinline getter: SharedPreferences.(String, T) -> T,
		crossinline setter: Editor.(String, T) -> Editor
) =
		object : ReadWriteProperty<Any?, T> {
			override fun getValue(thisRef: Any?, property: KProperty<*>): T =
					provide().getter(key ?: property.name, defaultValue)!!

			override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
					provide().edit().setter(key ?: property.name, value).apply()
		}


private inline fun <T> SharedPreferencesProvider.observableDelegate(
		defaultValue: T? = null,
		crossinline getter: SharedPreferences.(String, T?) -> T?,
		crossinline setter: Editor.(String, T?) -> Editor
): ReadOnlyProperty<Any?, ObservableField<T?>> = object : ObservableField<T?>(), ReadOnlyProperty<Any?, ObservableField<T?>> {
	var originalProperty: KProperty<*>? = null

	override fun getValue(thisRef: Any?, property: KProperty<*>): ObservableField<T?> {
		originalProperty = property
		return this
	}

	override fun get(): T? {
		val persistable by delegate(defaultValue, originalProperty!!.name, getter, setter)
		return super.get() ?: persistable ?: defaultValue
	}

	override fun set(value: T?) {
		super.set(value)
		var persistable by delegate(defaultValue, originalProperty!!.name, getter, setter)
		persistable = value
	}
}

private inline fun <T> SharedPreferencesProvider.observableDelegatePrimitive(
		defaultValue: T,
		crossinline getter: SharedPreferences.(String, T) -> T,
		crossinline setter: Editor.(String, T) -> Editor
): ReadOnlyProperty<Any?, ObservableField<T>> = object : ObservableField<T>(), ReadOnlyProperty<Any?, ObservableField<T>> {
	var originalProperty: KProperty<*>? = null

	override fun getValue(thisRef: Any?, property: KProperty<*>): ObservableField<T> {
		originalProperty = property
		return this
	}

	override fun get(): T {
		val persistable by delegatePrimitive(defaultValue, originalProperty!!.name, getter, setter)
		return super.get() ?: persistable ?: defaultValue
	}

	override fun set(value: T) {
		super.set(value)
		var persistable by delegatePrimitive(defaultValue, originalProperty!!.name, getter, setter)
		persistable = value
	}
}

fun SharedPreferencesProvider.int(def: Int = 0, key: String? = null): ReadWriteProperty<Any?, Int> = delegatePrimitive(def, key, SharedPreferences::getInt, Editor::putInt)
fun SharedPreferencesProvider.long(def: Long = 0, key: String? = null): ReadWriteProperty<Any?, Long> = delegatePrimitive(def, key, SharedPreferences::getLong, Editor::putLong)
fun SharedPreferencesProvider.float(def: Float = 0f, key: String? = null): ReadWriteProperty<Any?, Float> = delegatePrimitive(def, key, SharedPreferences::getFloat, Editor::putFloat)
fun SharedPreferencesProvider.boolean(def: Boolean = false, key: String? = null): ReadWriteProperty<Any?, Boolean> = delegatePrimitive(def, key, SharedPreferences::getBoolean, Editor::putBoolean)
fun SharedPreferencesProvider.stringSet(def: Set<String> = emptySet(), key: String? = null): ReadWriteProperty<Any?, Set<String>?> = delegate(def, key, SharedPreferences::getStringSet, Editor::putStringSet)
fun SharedPreferencesProvider.string(def: String? = null, key: String? = null): ReadWriteProperty<Any?, String?> = delegate(def, key, SharedPreferences::getString, Editor::putString)

fun SharedPreferencesProvider.observableInt(defaultValue: Int): ReadOnlyProperty<Any?, ObservableField<Int>> = observableDelegatePrimitive(defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt)
fun SharedPreferencesProvider.observableLong(defaultValue: Long): ReadOnlyProperty<Any?, ObservableField<Long>> = observableDelegatePrimitive(defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
fun SharedPreferencesProvider.observableFloat(defaultValue: Float): ReadOnlyProperty<Any?, ObservableField<Float>> = observableDelegatePrimitive(defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
fun SharedPreferencesProvider.observableBoolean(defaultValue: Boolean): ReadOnlyProperty<Any?, ObservableField<Boolean>> = observableDelegatePrimitive(defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
fun SharedPreferencesProvider.observableString(defaultValue: String? = null): ReadOnlyProperty<Any?, ObservableField<String?>> = observableDelegate(defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString)
fun SharedPreferencesProvider.observableStringSet(defaultValue: Set<String>? = null): ReadOnlyProperty<Any?, ObservableField<Set<String>?>> = observableDelegate(defaultValue, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet)

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
