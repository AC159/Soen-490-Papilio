package com.soen490chrysalis.papilio.view

import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.soen490chrysalis.papilio.databinding.ActivityCreateActivityBinding
import com.soen490chrysalis.papilio.view.dialogs.DatePickerFragment
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import com.soen490chrysalis.papilio.view.dialogs.TimePickerFragment
import com.soen490chrysalis.papilio.viewModel.CreateActivityViewModel
import com.soen490chrysalis.papilio.viewModel.factories.CreateActivityViewModelFactory

class CreateActivity : AppCompatActivity()
{
    private val logTag = CreateActivity::class.java.simpleName
    private lateinit var binding : ActivityCreateActivityBinding
    private lateinit var createActivityViewModel : CreateActivityViewModel

    private var pictureURIs : List<Uri> = ArrayList()
    private var startTime : EventTime = EventTime(0, 0)
    private var endTime : EventTime = EventTime(0, 0)

    private val c : Calendar = Calendar.getInstance()
    private var activityDate =
        EventDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Register the create activity view model
        val viewModelFactory = CreateActivityViewModelFactory()
        createActivityViewModel =
            ViewModelProvider(this, viewModelFactory)[CreateActivityViewModel::class.java]

        // Create Action Bar var so we can 1) display it with a proper title and 2) put a working back button on it
        val actionBar = supportActionBar

        // if Action Bar is not null, then put a back button on it as well as put the "User Profile" title on it
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Create Activity"
        }

        // Setup a listener on the create activity button
        binding.createActivityBtn.setOnClickListener {
            handleUserInputValidation()
        }

        binding.selectDateBtn.setOnClickListener {
            val datePicker = DatePickerFragment(
                ::activityDateCallback,
                activityDate.year,
                activityDate.month,
                activityDate.day
            )
            datePicker.show(supportFragmentManager, "Select Activity Date")
        }

        binding.selectStartTimeBtn.setOnClickListener {
            val startTimePicker =
                TimePickerFragment(::startTimeCallback, startTime.hourOfDay, startTime.minute)
            startTimePicker.show(supportFragmentManager, "Select Start Time")
        }

        binding.selectEndTimeBtn.setOnClickListener {
            val endTimePicker =
                TimePickerFragment(::endTimeCallback, endTime.hourOfDay, endTime.minute)
            endTimePicker.show(supportFragmentManager, "Select End Time")
        }

        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                // Callback is invoked after the user selects media items or closes the
                // photo picker.

                if (uris.size > 5)
                {
                    // Display a snackbar to the user that up to 5 pictures are allowed to be selected
                    // We need to make this check since not all phones support the photo picker
                    Snackbar.make(
                        binding.coordinatorLayoutCreateActivity,
                        "You can select up to 5 pictures!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else if (uris.isNotEmpty())
                {
                    pictureURIs = uris
                    Log.d(logTag, "Photo picker: # of pictures selected: ${uris.size}")
                    Log.d(logTag, "Uris: $uris")
                    val builder = SpannableStringBuilder()

                    for (uri in pictureURIs)
                    {
                        val cursor = contentResolver.query(uri, null, null, null, null)
                        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor?.moveToFirst()
                        val spannableString =
                            SpannableString(nameIndex?.let { cursor.getString(it).toString() })
                        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
                        builder.append(spannableString).append("\n")
                    }
                    binding.chosenFilesTv.visibility = View.VISIBLE
                    binding.chosenFilesTv.text = builder
                }
                else
                {
                    Log.d(logTag, "Photo picker: no pictures selected")
                }
            }

        // Setup a button listener to import up to 5 pictures
        binding.importPictureBtn.setOnClickListener {
            // Allow the user to only choose pictures and launch the photo picker
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun activityDateCallback(date : EventDate)
    {
        activityDate = date
        binding.selectDateBtn.text =
            "${activityDate.month + 1}/${activityDate.day}/${activityDate.year}"
    }

    private fun startTimeCallback(time : EventTime)
    {
        startTime = time
        if (startTime.minute == 0)
        {
            binding.selectStartTimeBtn.text = "${startTime.hourOfDay}h${startTime.minute}0"
        }
        else
        {
            binding.selectStartTimeBtn.text = "${startTime.hourOfDay}h${startTime.minute}"
        }
    }

    private fun endTimeCallback(time : EventTime)
    {
        endTime = time
        if (endTime.minute == 0)
        {
            binding.selectEndTimeBtn.text = "${endTime.hourOfDay}h${endTime.minute}0"
        }
        else
        {
            binding.selectEndTimeBtn.text = "${endTime.hourOfDay}h${endTime.minute}"
        }
    }

    private fun handleUserInputValidation()
    {
        val activityTitle : String = binding.eventTitle.text.toString()
        val description : String = binding.eventDescription.text.toString()
        val maxNbrOfParticipants : String = binding.eventMaxNumberParticipants.text.toString()

        val titleValidation = createActivityViewModel.validateActivityTitle(activityTitle)
        if (titleValidation != null)
        {
            binding.eventTitle.error = titleValidation
        }

        val descriptionValidation =
            createActivityViewModel.validateActivityDescription(description)
        if (descriptionValidation != null)
        {
            binding.eventDescription.error = descriptionValidation
        }

        val nbrOfParticipantsValidation =
            createActivityViewModel.validateActivityMaxNumberOfParticipants(maxNbrOfParticipants)
        if (nbrOfParticipantsValidation != null)
        {
            binding.eventMaxNumberParticipants.error = nbrOfParticipantsValidation
        }

        val picturesValidation =
            createActivityViewModel.validateActivityPictureUris(pictureURIs)
        if (picturesValidation != null)
        {
            binding.importPicturesTv.error = picturesValidation
        }
    }

    // This is the function that's called when the back button on the action bar is pressed
    override fun onOptionsItemSelected(item : MenuItem) : Boolean
    {
        when (item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}