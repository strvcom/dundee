package com.strv.dundee.ui.wallet

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class DatePickerDialogFragment() : DialogFragment() {

	companion object {
		private const val EXTRA_DATE = "date"

		fun newInstance(date: Date) = DatePickerDialogFragment().apply {
			val bundle = Bundle()
			bundle.putSerializable(EXTRA_DATE, date)
			arguments = bundle
		}
	}

	interface DatePickerListener {
		fun onDateSelected(date: Date)
	}

	var listener: DatePickerListener? = null
	lateinit var calendar: Calendar

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		isCancelable = true
		retainInstance = true

		val date = arguments?.getSerializable(EXTRA_DATE) as Date?

		calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
		calendar.time = date ?: Date()

		if (listener == null) throw ClassCastException("You must set ${DatePickerDialogFragment.javaClass.name} listener")
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		// cancelable on touch outside
		if (dialog != null) dialog.setCanceledOnTouchOutside(true)
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
			val newCalendar = Calendar.getInstance()
			newCalendar.set(Calendar.YEAR, year)
			newCalendar.set(Calendar.MONTH, month)
			newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
			listener?.onDateSelected(newCalendar.time)
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply { datePicker.maxDate = System.currentTimeMillis() }
	}

	override fun onDestroyView() {
		// http://code.google.com/p/android/issues/detail?id=17423
		if (dialog != null && retainInstance) dialog.setDismissMessage(null)
		super.onDestroyView()
	}
}