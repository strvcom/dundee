package com.strv.dundee

import com.strv.dundee.model.validateEmail
import com.strv.dundee.model.validatePassword
import org.amshove.kluent.shouldEqual
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test


@RunWith(RobolectricTestRunner::class)
class ValidatorsTest {

	@Test
	fun passwordValidator_correctPassword() {
		validatePassword("asd", 3) shouldEqual true
	}

	@Test
	fun passwordValidator1_correctPassword() {
		validatePassword("asdfghjkl", 3) shouldEqual true
	}

	@Test
	fun passwordValidator2_correctPassword() {
		validatePassword("asd", 0) shouldEqual true
	}

	@Test
	fun passwordValidator_wrongPassword() {
		validatePassword("asd", 5) shouldEqual false
	}

	@Test
	fun passwordValidator_wrongPassword_returnsFalse() {
		validatePassword("asd", 4) shouldEqual false
	}

	@Test
	fun emailValidator_correctEmail() {
		validateEmail("email@gmail.com") shouldEqual true
	}

	@Test
	fun emailValidator_wrongEmail() {
		validateEmail("emailgmail.com") shouldEqual false
	}

	@Test
	fun emailValidator_wrongEmail1() {
		validateEmail("email@gmailcom") shouldEqual false
	}


}