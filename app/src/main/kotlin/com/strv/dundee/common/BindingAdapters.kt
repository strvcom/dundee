package com.strv.dundee.common

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.graphics.Canvas
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.strv.dundee.R
import com.strv.ktools.Resource
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import java.lang.Exception


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

//Spinner
@BindingAdapter(value = *arrayOf("selection", "selectionAttrChanged", "adapter"), requireAll = false)
fun <T> setAdapter(view: Spinner, newSelection: T?, bindingListener: InverseBindingListener, adapter: ArrayAdapter<T>?) {
	view.adapter = adapter
	view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, p3: Long) {
			if (newSelection != view.getItemAtPosition(position))
				bindingListener.onChange()
		}

		override fun onNothingSelected(v: AdapterView<*>?) {}
	}

	if (newSelection != null) {
		val pos = (view.adapter as ArrayAdapter<T>).getPosition(newSelection)
		view.setSelection(pos)
	}
}

@InverseBindingAdapter(attribute = "selection", event = "selectionAttrChanged")
fun <T> getSelectedValue(view: Spinner): T {
	return view.selectedItem as T
}

@BindingAdapter("status")
fun setStatus(view: TextView, status: Resource.Status) {
	when (status) {
		Resource.Status.SUCCESS -> view.setTextColor(view.resources.getColor(R.color.status_success))
		Resource.Status.ERROR -> view.setTextColor(view.resources.getColor(R.color.status_error))
		Resource.Status.LOADING -> view.setTextColor(view.resources.getColor(R.color.status_loading))
	}
}

// Bottom Sheet state 2-way binding
@BindingAdapter("bottomSheetOpen", "bottomSheetStateChanged", requireAll = false)
fun setBottomSheetOpen(view: View, open: Boolean, bindingListener: InverseBindingListener) {
	val behavior = BottomSheetBehavior.from(view)
	behavior.setBottomSheetCallback(null)
	behavior.state = if (open) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_HIDDEN
	behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
		override fun onSlide(bottomSheet: View, slideOffset: Float) {}

		override fun onStateChanged(bottomSheet: View, newState: Int) {
			if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)
				bindingListener.onChange()
		}
	})
}

@InverseBindingAdapter(attribute = "bottomSheetOpen", event = "bottomSheetStateChanged")
fun getBottomSheetOpen(view: CardView): Boolean {
	val behavior = BottomSheetBehavior.from(view)
	return behavior.state == BottomSheetBehavior.STATE_COLLAPSED
}

@BindingAdapter("touchHelperCallback")
fun setTouchHelperCallback(recycler: RecyclerView, touchHelperCallback: TouchHelperCallback) {
	val foregroundTag = recycler.resources.getString(R.string.item_foreground_tag)

	val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
		override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
			return false
		}


		override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
			viewHolder?.itemView?.let {
				val foregroundView: View = viewHolder.itemView.findViewWithTag(foregroundTag)
				if(foregroundView == null) throw Exception("Item view doesn't have view with \"foreground\" tag")
				else ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
			}
		}

		override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
			viewHolder?.itemView?.let {
				val foregroundView: View = viewHolder.itemView.findViewWithTag(foregroundTag)
				if(foregroundView == null) throw Exception("Item view doesn't have view with \"foreground\" tag")
				else {
					if (dX > 0) {
//						binding.unmatchLeft.setVisibility(View.VISIBLE)
//						binding.unmatchRight.setVisibility(View.GONE)
					} else {
//						binding.unmatchLeft.setVisibility(View.GONE)
//						binding.unmatchRight.setVisibility(View.VISIBLE)
					}
					ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
				}
			}
		}

		override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
			viewHolder?.itemView?.let {
				val foregroundView: View = viewHolder.itemView.findViewWithTag(foregroundTag)
				if(foregroundView == null) throw Exception("Item view doesn't have view with \"foreground\" tag")
				else {
					if (dX > 0) {
//						binding.unmatchLeft.setVisibility(View.VISIBLE)
//						binding.unmatchRight.setVisibility(View.GONE)
					} else {
//						binding.unmatchLeft.setVisibility(View.GONE)
//						binding.unmatchRight.setVisibility(View.VISIBLE)
					}
					ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
				}
			}
		}

		override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
			viewHolder?.itemView?.let {
				val foregroundView: View? = viewHolder.itemView.findViewWithTag(foregroundTag)
				if(foregroundView == null) throw Exception("Item view doesn't have view with \"foreground\" tag")
				else ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
			}
		}

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
			val position = viewHolder?.adapterPosition ?: -1
			if (recycler.adapter is BindingRecyclerViewAdapter<*>) {
				val adapter = recycler.adapter as BindingRecyclerViewAdapter<*>
				val item = adapter.getAdapterItem(position)
				item?.let { touchHelperCallback.onItemSwiped(item) }
			}
		}
	}

	val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
	itemTouchHelper.attachToRecyclerView(recycler)
}