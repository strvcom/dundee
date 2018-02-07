package com.strv.dundee.test.mock

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry

class MockLifecycleOwner : LifecycleOwner {
	private val lifecycleRegistry = LifecycleRegistry(this)
	override fun getLifecycle() = lifecycleRegistry

	fun activate() {
		lifecycle.markState(Lifecycle.State.RESUMED)
	}

	fun deactivate() {
		lifecycle.markState(Lifecycle.State.DESTROYED)
	}

}