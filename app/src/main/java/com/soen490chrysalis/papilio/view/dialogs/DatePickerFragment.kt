package com.soen490chrysalis.papilio.view.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import kotlin.reflect.KFunction1

data class EventDate(var year : Int, var month : Int, var day : Int)

class DatePickerFragment(
    val onDateSetCallback : KFunction1<EventDate, Unit>,
    private val year : Int,
    private val month : Int,
    private val day : Int
) :
        DialogFragment(), DatePickerDialog.OnDateSetListener
{
    private val c : Calendar = Calendar.getInstance()
    private var eventDate =
        EventDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // Create a new instance of DatePickerDialog and return it
        val dialog = DatePickerDialog(requireContext(), this, year, month, day)
        dialog.datePicker.minDate = c.timeInMillis // set the min date to today
        return dialog
    }

    override fun onDateSet(view : DatePicker, year : Int, month : Int, day : Int)
    {
        Log.d("DatePickerFragment", "onDateSet - ${month}/${day}/${year} (mm/dd/yyyy)")
        eventDate.year = year
        eventDate.month = month // months start with 0 for some reason
        eventDate.day = day
        onDateSetCallback(eventDate)
    }
}