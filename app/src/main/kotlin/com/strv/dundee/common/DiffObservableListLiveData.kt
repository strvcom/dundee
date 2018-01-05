package com.strv.dundee.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.strv.dundee.model.repo.common.Resource
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


class DiffObservableListLiveData<T>(liveData: LiveData<Resource<List<T>>>, callback: DiffObservableList.Callback<T>) : MediatorLiveData<Resource<List<T>>>() {
	val diffList = DiffObservableList<T>(callback)

	init {
		addSource(liveData, {
			value = it
			it?.data?.let { diffList.update(it) }
		})
	}
}

@BindingAdapter(value = *arrayOf("liveDataItemBinding", "liveDataItems", "liveDataAdapter"), requireAll = false)
fun <T> setAdapterLiveData(recyclerView: RecyclerView, liveDataItemBinding: ItemBinding<T>, liveDataItems: DiffObservableListLiveData<T>, presetAdapter: BindingRecyclerViewAdapter<T>?) {
	val oldAdapter = recyclerView.adapter as BindingRecyclerViewAdapter<T>?
	val adapter: BindingRecyclerViewAdapter<T>
	if (presetAdapter == null) {
		if (oldAdapter == null) {
			adapter = BindingRecyclerViewAdapter()
		} else {
			adapter = oldAdapter
		}
	} else {
		adapter = presetAdapter
	}
	if (oldAdapter !== adapter) {
		adapter.setItemBinding(liveDataItemBinding)
		adapter.setItems(liveDataItems.diffList)
	}

	if (oldAdapter !== adapter) {
		recyclerView.adapter = adapter
	}
}