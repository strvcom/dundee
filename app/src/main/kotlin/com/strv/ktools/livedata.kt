package com.strv.ktools

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.util.Log

/**
 * Live Data variation used for event-based communication from ViewModel to Activity/Fragment
 *
 * Simply create an instance in ViewModel, observe the instance in Activity/Fragment the same way as any other LiveData and when you need to trigger the event,
 * call @see EventLiveData.publish(T).
 */
class EventLiveData<T> : MutableLiveData<T>() {
	private var pending = false

	override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
		if (hasActiveObservers()) {
			Log.w("EventLiveData", "Multiple observers registered but only one will be notified of changes.")
		}

		// Observe the internal MutableLiveData
		super.observe(owner, Observer {
			if (pending) {
				pending = false
				observer.onChanged(it)
			}
		})
	}

	override fun setValue(t: T?) {
		pending = true
		super.setValue(t)
	}

	fun publish(value: T) {
		setValue(value)
	}
}

/**
 * Shorthand for EventLiveData where you don't need to pass any value
 */
fun EventLiveData<Unit>.publish() {
	publish(Unit)
}

/**
 * Shorthand for adding source to MediatorLiveData and assigning its value - great for validators, chaining live data etc.
 */
fun <S, T> MediatorLiveData<T>.addValueSource(source: LiveData<S>, resultFunction: (sourceValue: S?) -> T) = this.apply { addSource(source, { value = resultFunction(it) }) }

/**
 * Shorthand for mapping LiveData instead of using static methods from Transformations
 */
fun <S, T> LiveData<T>.map(mapFunction: (T) -> S) = Transformations.map(this, mapFunction)

/**
 * Shorthand for switch mapping LiveData instead of using static methods from Transformations
 */
fun <S, T> LiveData<T>.switchMap(switchMapFunction: (T) -> LiveData<S>) = Transformations.switchMap(this, switchMapFunction)