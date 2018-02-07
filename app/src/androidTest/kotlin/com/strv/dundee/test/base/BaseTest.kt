package com.strv.dundee.test.base

import android.support.test.InstrumentationRegistry

abstract class BaseTest {

	private var asyncLock: AsyncLock? = null

	protected fun lockAsync() {
		asyncLock = AsyncLock()
		asyncLock!!.lock()
	}

	protected fun unlockAsync() {
		asyncLock?.unlock()
	}

	fun runOnMain(runnable: () -> Unit) {
		InstrumentationRegistry.getInstrumentation().runOnMainSync(runnable)
	}
}