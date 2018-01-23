package com.strv.dundee.common

import android.support.annotation.IdRes

interface ActionDoneCallback {
	fun onActionDone()
}

interface OnItemClickListener<in T> {
	fun onItemClick(item: T)
}

abstract class TouchHelperCallback<in T>(@IdRes val itemForegroundViewId: Int, @IdRes val itemLeftViewId: Int, @IdRes val itemRightViewId: Int) {
	abstract fun onItemSwiped(item: T)
}