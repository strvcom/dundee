package com.strv.dundee.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.strv.dundee.databinding.ItemTickerBinding
import com.strv.dundee.model.entity.ExchangeRate
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.Resource


class TickerView : FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
	private var rootBinding: ItemTickerBinding

	var ticker: Resource<Ticker>? = null
		set(value) {
			rootBinding.ticker = value?.data
		}

	var exchangeRate: Resource<ExchangeRate>? = null
		set(value) {
			rootBinding.exchangeRate = value?.data
		}

	var currency: String? = null
		set(value) {
			rootBinding.currency = value
		}

	var coin: String? = null
		set(value) {
			rootBinding.coin = value
		}

	init {
		rootBinding = ItemTickerBinding.inflate(layoutInflater, this, true)
	}
}