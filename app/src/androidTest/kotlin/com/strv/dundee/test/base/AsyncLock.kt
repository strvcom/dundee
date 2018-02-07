package com.strv.dundee.test.base

import java.util.concurrent.CountDownLatch

class AsyncLock {

	private val signal = CountDownLatch(1)

	fun lock() {
		try {
			signal.await()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}

	}

	fun unlock() {
		signal.countDown()
	}
}