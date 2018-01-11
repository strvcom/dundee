package com.strv.dundee.common

import android.support.annotation.IdRes


interface ActionDoneCallback {
	fun onActionDone()
}

interface OnItemClickListener<in T> {
	fun onItemClick(item: T)
}

interface TouchHelperCallback<in T> {
	fun onItemSwiped(item: T)
	@IdRes
	fun getItemForegroundViewId(): Int? = null

	@IdRes
	fun getItemLeftViewId(): Int? = null

	@IdRes
	fun getItemRightViewId(): Int? = null
}