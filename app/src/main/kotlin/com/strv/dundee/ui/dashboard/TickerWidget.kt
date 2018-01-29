package com.strv.dundee.ui.dashboard

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.strv.dundee.databinding.ItemTickerBinding
import com.strv.dundee.model.entity.ExchangeRates
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.Resource

class TickerWidget : FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
	private var rootBinding: ItemTickerBinding

	var ticker: Resource<Ticker>? = null
		set(value) {
			rootBinding.ticker = value?.data
		}

	var exchangeRates: HashMap<String, LiveData<Resource<ExchangeRates>>>? = null
		set(value) {
			rootBinding.exchangeRates = value
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