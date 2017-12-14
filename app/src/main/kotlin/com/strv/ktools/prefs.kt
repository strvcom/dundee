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
import android.preference.PreferenceManager
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

fun SharedPreferencesProvider.int(def: Int = 0, key: String? = null): ReadWriteProperty<Any?, Int> = delegatePrimitive(def, key, SharedPreferences::getInt, Editor::putInt)

fun SharedPreferencesProvider.long(def: Long = 0, key: String? = null): ReadWriteProperty<Any?, Long> = delegatePrimitive(def, key, SharedPreferences::getLong, Editor::putLong)

fun SharedPreferencesProvider.float(def: Float = 0f, key: String? = null): ReadWriteProperty<Any?, Float> = delegatePrimitive(def, key, SharedPreferences::getFloat, Editor::putFloat)

fun SharedPreferencesProvider.boolean(def: Boolean = false, key: String? = null): ReadWriteProperty<Any?, Boolean> = delegatePrimitive(def, key, SharedPreferences::getBoolean, Editor::putBoolean)

fun SharedPreferencesProvider.stringSet(def: Set<String> = emptySet(), key: String? = null): ReadWriteProperty<Any?, Set<String>?> = delegate(def, key, SharedPreferences::getStringSet, Editor::putStringSet)

fun SharedPreferencesProvider.string(def: String? = null, key: String? = null): ReadWriteProperty<Any?, String?> = delegate(def, key, SharedPreferences::getString, Editor::putString)


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
