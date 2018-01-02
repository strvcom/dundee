package com.strv.dundee.model.entity

object Currency {
    const val USD = "USD"
    const val EUR = "EUR"

    fun getAll() = arrayOf(Currency.USD, Currency.EUR)
}