package com.strv.dundee

import com.strv.dundee.model.validatePassword
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class ValidatorsTest {


	@Test
	fun passwordValidator_correctPassword_returnsTrue() {
		validatePassword("asd", 3) shouldEqual true
	}


}