package com.strv.dundee.test

import android.arch.lifecycle.Observer
import android.support.test.runner.AndroidJUnit4
import com.strv.dundee.test.base.BaseTest
import com.strv.dundee.test.base.TestViewModelProvider
import com.strv.dundee.test.mock.MockLifecycleOwner
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculationsTest : BaseTest() {

	@Test
	fun recalculateTotal() {
		runOnMain {
			val lifecycleOwner = MockLifecycleOwner()
			val dashboardViewModel = TestViewModelProvider.getDashboardViewModel()
			dashboardViewModel.wallets.observe(lifecycleOwner, Observer {})
			lifecycleOwner.activate()
			dashboardViewModel.recalculateTotal() shouldEqual 1
		}
	}

}