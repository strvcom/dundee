package com.strv.ktools

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.strv.dundee.BR
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// ViewModelBinding extension functions
inline fun <reified VM : ViewModel, B : ViewDataBinding> FragmentActivity.vmb(@LayoutRes layoutResId: Int, viewModelProvider: ViewModelProvider? = null) = object : ReadOnlyProperty<FragmentActivity, ViewModelBinding<VM, B>> {
	var instance = ViewModelBinding<VM, B>(this@vmb, VM::class.java, layoutResId, viewModelProvider, null)
	override fun getValue(thisRef: FragmentActivity, property: KProperty<*>) = instance
}

inline fun <reified VM : ViewModel, B : ViewDataBinding> FragmentActivity.vmb(@LayoutRes layoutResId: Int, noinline viewModelFactory: () -> VM) = object : ReadOnlyProperty<FragmentActivity, ViewModelBinding<VM, B>> {
	var instance = ViewModelBinding<VM, B>(this@vmb, VM::class.java, layoutResId, null, viewModelFactory)
	override fun getValue(thisRef: FragmentActivity, property: KProperty<*>) = instance
}

inline fun <reified VM : ViewModel, B : ViewDataBinding> Fragment.vmb(@LayoutRes layoutResId: Int, viewModelProvider: ViewModelProvider? = null) = object : ReadOnlyProperty<Fragment, ViewModelBinding<VM, B>> {
	var instance = ViewModelBinding<VM, B>(this@vmb, VM::class.java, layoutResId, viewModelProvider, null)
	override fun getValue(thisRef: Fragment, property: KProperty<*>) = instance
}

inline fun <reified VM : ViewModel, B : ViewDataBinding> Fragment.vmb(@LayoutRes layoutResId: Int, noinline viewModelFactory: () -> VM) = object : ReadOnlyProperty<Fragment, ViewModelBinding<VM, B>> {
	var instance = ViewModelBinding<VM, B>(this@vmb, VM::class.java, layoutResId, null, viewModelFactory)
	override fun getValue(thisRef: Fragment, property: KProperty<*>) = instance
}

// ViewModelBinding class itself
class ViewModelBinding<VM : ViewModel, B : ViewDataBinding> constructor(
	private val lifecycleOwner: LifecycleOwner,
	val viewModelClass: Class<VM>,
	@LayoutRes val layoutResId: Int,
	var viewModelProvider: ViewModelProvider?,
	val viewModelFactory: (() -> VM)?
) {
	init {
		if (!(lifecycleOwner is FragmentActivity || lifecycleOwner is Fragment))
			throw IllegalArgumentException("Provided LifecycleOwner must be one of FragmentActivity or Fragment")
	}

	val binding: B by lazy {
		initializeVmb()
		DataBindingUtil.inflate<B>(activity.layoutInflater, layoutResId, null, false)!!
	}

	val viewModel: VM by lazy {
		initializeVmb()
		viewModelProvider!!.get(viewModelClass)
	}
	val rootView by lazy { binding.root }

	val fragment: Fragment? = if (lifecycleOwner is Fragment) lifecycleOwner else null
	val activity: FragmentActivity by lazy {
		if (lifecycleOwner is FragmentActivity) lifecycleOwner else (lifecycleOwner as Fragment).activity!!
	}

	private var initialized = false

	init {
		lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
			@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
			fun onCreate() {
				binding.setLifecycleOwner(lifecycleOwner)
				// setup binding variables
				binding.setVariable(BR.viewModel, viewModel)
				binding.setVariable(BR.view, fragment ?: activity)

				if (viewModel is LifecycleReceiver)
					(viewModel as LifecycleReceiver).onLifecycleReady(lifecycleOwner)

				if (lifecycleOwner is Activity)
					activity.setContentView(binding.root)
			}
		})
	}

	private fun initializeVmb() {
		if (initialized) return
		if (viewModelFactory != null) {
			val factory = object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel?> create(modelClass: Class<T>) = viewModelFactory.invoke() as T
			}
			if (viewModelProvider == null)
				viewModelProvider = if (fragment != null) ViewModelProviders.of(fragment, factory) else ViewModelProviders.of(activity, factory)
		} else {
			if (viewModelProvider == null)
				viewModelProvider = if (fragment != null) ViewModelProviders.of(fragment) else ViewModelProviders.of(activity)
		}
		initialized = true
	}
}

interface LifecycleReceiver {
	fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {}
}

// extension functions connecting LiveData with ObservableField
fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observableField: ObservableField<T>) {
	this.observe(lifecycleOwner, android.arch.lifecycle.Observer { observableField.set(it) })
}

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner): ObservableField<T> {
	val observableField = ObservableField<T>()
	this.observe(lifecycleOwner, android.arch.lifecycle.Observer { observableField.set(it) })
	return observableField
}

fun <T> ObservableField<T>.observe(observer: (T?) -> Unit) {
	this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
		override fun onPropertyChanged(p0: Observable?, p1: Int) {
			observer(this@observe.get())
		}
	})
}

// single-event LiveData
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

fun EventLiveData<Unit>.publish() {
	publish(Unit)
}

class LiveBus<T> {
	val observers = HashMap<LifecycleOwner, Observer<T>>()

	fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
		observers[lifecycleOwner] = observer
	}

	fun post(value: T) {
		observers.keys.forEach { lifecycleOwner ->
			if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
				observers[lifecycleOwner]?.onChanged(value)
			}
		}
	}
}