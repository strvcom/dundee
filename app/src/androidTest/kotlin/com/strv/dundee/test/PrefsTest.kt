package com.strv.dundee.test

import android.arch.lifecycle.Observer
import android.content.Context
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.strv.dundee.test.base.BaseTest
import com.strv.dundee.test.mock.MockLifecycleOwner
import com.strv.ktools.sharedPrefs
import com.strv.ktools.stringLiveData
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrefsTest : BaseTest() {
	val context = InstrumentationRegistry.getInstrumentation().context

	@Before
	fun init() {

	}

	@Test
	fun test_isSavedToPrefs_default() {
		runOnMain {
			val pref1 by context.sharedPrefs().stringLiveData()
			pref1.value = "test"
			val savedValue = PreferenceManager.getDefaultSharedPreferences(context).getString("pref1", null)
			savedValue shouldEqual pref1.value
		}
	}

	@Test
	fun test_isSavedToPrefs_custom() {
		runOnMain {
			val pref1 by context.sharedPrefs("my_prefs").stringLiveData()
			pref1.value = "test"
			val savedValueCorrect = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE).getString("pref1", null)
			savedValueCorrect shouldEqual pref1.value

			val savedValueWrong1 = context.getSharedPreferences("other_prefs", Context.MODE_PRIVATE).getString("pref1", null)
			savedValueWrong1 shouldNotEqual pref1.value

			val savedValueWrong2 = PreferenceManager.getDefaultSharedPreferences(context).getString("pref1", null)
			savedValueWrong2 shouldNotEqual pref1.value
		}
	}

	@Test
	fun test_isSyncedLiveData() {
		runOnMain {
			val lifecycleOwner = MockLifecycleOwner()

			val pref1 by context.sharedPrefs("my_prefs").stringLiveData()
			val pref2 by context.sharedPrefs("my_prefs").stringLiveData(key = "pref1")

			pref1.observe(lifecycleOwner, Observer {
			})

			pref2.observe(lifecycleOwner, Observer {
				it?.let {
					it shouldEqual "test"
				}
			})

			lifecycleOwner.activate()
			pref1.value = "test"
		}
	}

}