package com.sto_opka91.todoapp2.fragments.dialog
import android.content.Context
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class TimePickerHelper (
    private val context: Context,
    private val onDateTimeSelected: (Long) -> Unit
) {

    private val calendar: Calendar = Calendar.getInstance()

    fun showDateTimePicker() {
        val dpd = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dpd.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "DatePickerDialog")
    }

    private fun showTimePicker() {
        val tpd = TimePickerDialog.newInstance(
            { _, hourOfDay, minute, second ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val selectedDateTimeInMillis = calendar.timeInMillis
                onDateTimeSelected.invoke(selectedDateTimeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        tpd.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "TimePickerDialog")
    }
}