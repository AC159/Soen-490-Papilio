package com.soen490chrysalis.papilio.view.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import kotlin.reflect.KFunction1

data class EventTime(var hourOfDay : Int, var minute : Int)

class TimePickerFragment(
    val onTimeSetCallback : KFunction1<EventTime, Unit>,
    private val hourOfDay : Int,
    private val minute : Int
) : DialogFragment(), TimePickerDialog.OnTimeSetListener
{
    private var eventTime : EventTime = EventTime(0, 0)

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hourOfDay, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view : TimePicker, hourOfDay : Int, minute : Int)
    {
        Log.d("TimePickerFragment", "onTimeSet - ${hourOfDay}h${minute}")
        eventTime.hourOfDay = hourOfDay
        eventTime.minute = minute
        onTimeSetCallback(eventTime)
    }
}