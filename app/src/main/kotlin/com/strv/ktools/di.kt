package com.strv.ktools

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val DI_SCOPE_GLOBAL = "#__global"

open class DIProvider<T>(val provider: () -> T)
class SingletonDIProvider<T>(provider: () -> T) : DIProvider<T>(provider) {
	val instance by lazy { provider() }
}

object DIStorage {
	private val provided = HashMap<String, HashMap<String, DIProvider<Any>>?>()

	fun get(scope: String, className: String) = provided[scope]?.get(className)
	fun put(scope: String, className: String, provider: DIProvider<Any>) {
		if (!provided.containsKey(scope))
			provided[scope] = hashMapOf()
		provided[scope]!![className] = provider
	}
}

// public methods
inline fun <reified T : Any> inject(scope: String = DI_SCOPE_GLOBAL) = object : ReadOnlyProperty<Any?, T> {
	var value: T? = null
	override fun getValue(thisRef: Any?, property: KProperty<*>): T {
		if (value != null) return value!!

		val found = DIStorage.get(scope, T::class.java.name)
			?: throw IllegalStateException("Dependency for property ${property.name}: ${T::class.java.name} not provided.")
		return when (found) {
			is SingletonDIProvider -> found.instance as T
			else -> found.provider.invoke() as T
		}.also { value = it }
	}

}

inline fun <reified T : Any> provide(scope: String = DI_SCOPE_GLOBAL, noinline provider: () -> T) = DIStorage.put(scope, T::class.java.name, DIProvider(provider))
inline fun <reified T : Any> provideSingleton(scope: String = DI_SCOPE_GLOBAL, noinline provider: () -> T) = DIStorage.put(scope, T::class.java.name, SingletonDIProvider(provider))