package com.strv.dundee.common

import android.databinding.BindingAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText


@BindingAdapter("hide")
fun setHide(view: View, hide: Boolean) {
	view.visibility = if (hide) View.GONE else View.VISIBLE
}

@BindingAdapter("show")
fun setShow(view: View, show: Boolean) {
	view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("invisible")
fun setInvisible(view: View, invisible: Boolean) {
	view.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

interface ActionDoneCallback {
	fun onActionDone()
}

@BindingAdapter("onActionDoneCallback")
fun setOnActionDoneCallback(view: EditText, callback: ActionDoneCallback) {
	view.setOnEditorActionListener { _, actionId, _ ->
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			callback.onActionDone()
			true
		} else false
	}
}

interface TextChangedCallback {
	fun onTextChanged()
}

@BindingAdapter("onTextChangedCallback")
fun setOnTextChangedCallback(view: EditText, callback: TextChangedCallback) {
	view.addTextChangedListener(object : TextWatcher {
		override fun afterTextChanged(editable: Editable?) {
			callback.onTextChanged()
		}

		override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

		}

		override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

		}
	})
}