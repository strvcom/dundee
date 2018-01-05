package com.strv.dundee.common

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

// TODO: Temp fix for tatarka - remove when tatarka adds support for lifecycle
class LifecycleAwareBindingRecyclerViewAdapter<T>(val lifecycleOwner: LifecycleOwner) : BindingRecyclerViewAdapter<T>() {
	override fun onCreateBinding(inflater: LayoutInflater, @LayoutRes layoutId: Int, viewGroup: ViewGroup): ViewDataBinding {
		val binding = super.onCreateBinding(inflater, layoutId, viewGroup)
		binding.setLifecycleOwner(lifecycleOwner)
		return binding
	}
}