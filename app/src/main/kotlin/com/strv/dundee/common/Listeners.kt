package com.strv.dundee.common

import android.support.annotation.IdRes


interface ActionDoneCallback {
	fun onActionDone()
}

interface OnItemClickListener{
	fun <T>onItemClick(item: T)
}

interface TouchHelperCallback {
	fun <T>onItemSwiped(item: T)
	@IdRes
	fun getItemForegroundViewId(): Int? = null
	@IdRes
	fun getItemLeftViewId(): Int? = null
	@IdRes
	fun getItemRightViewId(): Int? = null
}