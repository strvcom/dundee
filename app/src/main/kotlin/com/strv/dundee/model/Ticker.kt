package com.strv.dundee.model


data class Ticker(
        val lastPrice: Double,
        val highPrice: Double,
        val lowPrice: Double,
        val timestamp: Long
)