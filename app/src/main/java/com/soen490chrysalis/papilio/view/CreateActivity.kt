package com.soen490chrysalis.papilio.view

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.R
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar
import com.mapbox.search.autofill.Query
import com.soen490chrysalis.papilio.databinding.ActivityCreateActivityBinding
import com.soen490chrysalis.papilio.view.dialogs.DatePickerFragment
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import com.soen490chrysalis.papilio.view.dialogs.TimePickerFragment
import com.soen490chrysalis.papilio.viewModel.CreateActivityViewModel
import com.soen490chrysalis.papilio.viewModel.factories.CreateActivityViewModelFactory
import java.io.InputStream


class CreateActivity : AppCompatActivity()
{
    private val logTag = CreateActivity::class.java.simpleName
    private val PERMISSIONS_REQUEST_LOCATION = 0
    private lateinit var binding : ActivityCreateActivityBinding
    private lateinit var createActivityViewModel : CreateActivityViewModel

    /* Each picture is represented as a pair where the first element is the image file extension
       and the second value is the input stream to that image */
    private var pictures : MutableList<Pair<String, InputStream>> = ArrayList()
    private var startTime : EventTime = EventTime(-1, -1)
    private var endTime : EventTime = EventTime(-1, -1)

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
            actionBar.title = "Create an Activity"
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
                    pictures.clear()

                    Log.d(logTag, "Photo picker: # of pictures selected: ${uris.size}")
                    Log.d(logTag, "Uris: $uris")

                    val builder = SpannableStringBuilder()

                    for (uri in uris)
                    {
                        Log.d(logTag, "Image uri: $uri")

                        val cursor = contentResolver.query(
                            uri,
                            arrayOf(
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                            ),
                            null,
                            null,
                            null
                        )

                        cursor?.moveToFirst()
                        val fileName = cursor?.getString(0)
                        val spannableString =
                            SpannableString(fileName)
                        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
                        builder.append(spannableString).append("\n")

                        val inputStream = contentResolver.openInputStream(uri)
                        if (inputStream != null)
                        {
                            println("Number of bytes in the file: ${inputStream.available()}")
                            val tokens = fileName?.split(".")
                            val fileExtension : String = tokens?.get(tokens.size - 1) ?: "jpg"
                            pictures.add(Pair(fileExtension, inputStream))
                        }
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

        binding.eventLocation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSIONS_REQUEST_LOCATION
                )
            }
        }

        binding.eventLocation.addTextChangedListener {

            val userAddress : String = binding.eventLocation.text.toString()

            /* We will only ask mapbox to give us address suggestions once the user has put
               enough text because otherwise the suggestions are not accurate and we are wasting
               API calls */
            if (userAddress.length >= 15)
            {
                val query : Query? = Query.create(userAddress)

                if (query != null)
                {
                    createActivityViewModel.getMapBoxAddressSuggestions(query)
                }
            }
        }

        createActivityViewModel.activityAddressSuggestions.observe(this, Observer<List<String>> {
            val adapter : ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, it)

            binding.eventLocation.setAdapter(adapter)
            adapter.notifyDataSetChanged() // Force to show the dropdown of address suggestions
        })

        // Setup a listener on the create activity button
        binding.createActivityBtn.setOnClickListener {
            handleUserInputValidation()
            Log.d(logTag, "Final activity location is ${binding.eventLocation.text}")
        }

        // Listen on request response when the user sends a new activity
        createActivityViewModel.postNewUserActivityResponse.observe(this) { _response ->
            pictures.clear() // we clear this list so as to avoid having IO exceptions on already close input file streams
            binding.chosenFilesTv.visibility = View.GONE
            binding.chosenFilesTv.text = null

            displaySnackBar(binding.coordinatorLayoutCreateActivity, _response.msg)
        }
    }

    @Suppress("SameParameterValue")
    private fun isPermissionGranted(permission : String) : Boolean
    {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
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
        val individualCost : String = binding.eventIndividualCost.text.toString()
        val groupCost : String = binding.eventGroupCost.text.toString()

        val dateValidation =
            createActivityViewModel.validateActivityDate(binding.selectDateBtn.text.toString())
        binding.selectDateBtn.error = dateValidation

        val validateStartTime = createActivityViewModel.validateStartTime(startTime)
        binding.selectStartTimeBtn.error = validateStartTime

        val validateEndTime = createActivityViewModel.validateEndTime(endTime)
        binding.selectEndTimeBtn.error = validateEndTime

        val titleValidation = createActivityViewModel.validateActivityTitle(activityTitle)
        binding.eventTitle.error = titleValidation

        val descriptionValidation =
            createActivityViewModel.validateActivityDescription(description)
        binding.eventDescription.error = descriptionValidation

        val nbrOfParticipantsValidation =
            createActivityViewModel.validateActivityMaxNumberOfParticipants(maxNbrOfParticipants)
        binding.eventMaxNumberParticipants.error = nbrOfParticipantsValidation

        val nbrOfIndividualCostValidation =
            createActivityViewModel.validateActivityIndividualCost(individualCost)
        binding.eventIndividualCost.error = nbrOfIndividualCostValidation

        val nbrOfGroupCostValidation =
            createActivityViewModel.validateActivityGroupCost(groupCost)
        binding.eventGroupCost.error = nbrOfGroupCostValidation

        val picturesValidation =
            createActivityViewModel.validateActivityPictureUris(pictures)
        binding.importPicturesTv.error = picturesValidation

        val addressValidation = createActivityViewModel.validateActivityAddress()
        binding.eventLocation.error = addressValidation

        if (titleValidation == null && descriptionValidation == null && nbrOfParticipantsValidation == null
                && nbrOfIndividualCostValidation == null && nbrOfGroupCostValidation == null
                && picturesValidation == null && addressValidation == null && validateStartTime == null
                && validateEndTime == null && dateValidation == null
        )
        {
            val spec =
                CircularProgressIndicatorSpec(
                    this, null, 0,
                    R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
                )
            val progressIndicatorDrawable =
                IndeterminateDrawable.createCircularDrawable(this, spec)
            binding.createActivityBtn.icon = progressIndicatorDrawable

            // disable the 'create activity' button while we process the request
            binding.createActivityBtn.isEnabled = false

            createActivityViewModel.postNewActivity(
                activityTitle,
                description,
                Integer.parseInt(individualCost),
                Integer.parseInt(groupCost),
                Integer.parseInt(maxNbrOfParticipants),
                pictures,
                activityDate,
                startTime,
                endTime
            )
        }
    }

    // Utility function that displays a snackbar in case of success/errors
    private fun displaySnackBar(coordinatorLayout : CoordinatorLayout, msg : String)
    {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show()
        binding.createActivityBtn.icon = null
        binding.createActivityBtn.isEnabled = true // allow the user to make another request
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