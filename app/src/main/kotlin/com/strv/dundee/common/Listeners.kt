package com.strv.dundee.common


interface ActionDoneCallback {
	fun onActionDone()
}

interface OnItemClickListener{
	fun <T>onItemClick(item: T)
}

interface TouchHelperCallback {
	fun <T>onItemSwiped(item: T)
}