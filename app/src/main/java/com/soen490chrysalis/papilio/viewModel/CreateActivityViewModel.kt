package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autofill.*
import com.soen490chrysalis.papilio.BuildConfig
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

data class PostNewUserActivityResponse(var isSuccess : Boolean, var msg : String)

class CreateActivityViewModel(private val activityRepository : IActivityRepository) : ViewModel()
{
    private val logTag = CreateActivityViewModel::class.java.simpleName
    var activityAddressSuggestions : MutableLiveData<List<String>> =
        MutableLiveData<List<String>>(ArrayList())

    /* We keep track of whole mapbox address suggestions because they contain lat/long coordinates
       which will be useful when we want to display the activity location on a map
    */
    private var fullActivityAddresses : List<AddressAutofillSuggestion> = ArrayList()

    var postNewUserActivityResponse : MutableLiveData<PostNewUserActivityResponse> =
        MutableLiveData()

    fun validateActivityTitle(title : String) : String?
    {
        if (title.length >= 3) return null
        return "Title must be at least 3 characters long!"
    }

    fun validateActivityDescription(description : String) : String?
    {
        if (description.length >= 15) return null
        return "Description must be at least 15 characters long!"
    }

    fun validateActivityMaxNumberOfParticipants(maxNbrOfParticipants : String) : String?
    {
        try
        {
            val nbrOfParticipants = Integer.parseInt(maxNbrOfParticipants)
            if (nbrOfParticipants >= 1) return null
        }
        catch (e : java.lang.NumberFormatException)
        {
            return "Not a number!"
        }
        return "Number of participants must be greater than 0!"
    }

    fun validateActivityIndividualCost(individualCost : String) : String?
    {
        try
        {
            val nbrOfIndividualCost = Integer.parseInt(individualCost)
            if (nbrOfIndividualCost >= 0) return null
        }
        catch (e : java.lang.NumberFormatException)
        {
            return "Not a number!"
        }
        return "Number of participants must be greater than or equal to 0!"
    }

    fun validateActivityGroupCost(groupCost : String) : String?
    {
        try
        {
            val nbrOfGroupCost = Integer.parseInt(groupCost)
            if (nbrOfGroupCost >= 0) return null
        }
        catch (e : java.lang.NumberFormatException)
        {
            return "Not a number!"
        }
        return "Number of participants must be greater than or equal to 0!"
    }

    fun validateActivityPictureUris(pictures : List<Pair<String, InputStream>>) : String?
    {
        if (pictures.isNotEmpty()) return null
        return "Don't forget to add some pictures!"
    }

    fun validateActivityDate(date : String) : String?
    {
        println("Date validation: ${date.trim(' ').lowercase(Locale.getDefault())}")
        if (date.trim(' ').lowercase(Locale.getDefault()) != "select date") return null
        return "You must select a date!"
    }

    fun validateStartTime(startTime : EventTime) : String?
    {
        if (startTime.hourOfDay != -1 && startTime.minute != -1) return null
        return "You must select a start time!"
    }

    fun validateEndTime(endTime : EventTime) : String?
    {
        if (endTime.hourOfDay != -1 && endTime.minute != -1) return null
        return "You must select an end time!"
    }

    fun validateActivityAddress() : String?
    {
        if (activityAddressSuggestions.value?.isNotEmpty() == true) return null
        return "You must select an address from the dropdown!"
    }

    fun getMapBoxAddressSuggestions(query : Query)
    {
        viewModelScope.launch {
            val addressAutofill = AddressAutofill.create(BuildConfig.MAPBOX_SECRET_TOKEN)
            val response : AddressAutofillResponse =
                addressAutofill.suggestions(query, AddressAutofillOptions())

            when (response)
            {
                is AddressAutofillResponse.Suggestions ->
                {
                    Log.d(logTag, response.suggestions.toString())
                    val nbrOfSuggestions = response.suggestions.size

                    if (nbrOfSuggestions > 0)
                    {
                        // Let's take at most the top 5 address suggestions which we will display to the user
                        val topSuggestions : MutableList<String> = ArrayList()
                        val fullAddresses : MutableList<AddressAutofillSuggestion> = ArrayList()

                        for (i in 1..5)
                        {
                            if (nbrOfSuggestions - i >= 0)
                            {
                                val address = response.suggestions[nbrOfSuggestions - i]
                                topSuggestions.add(address.formattedAddress)
                                fullAddresses.add(address)
                            }
                            else break
                        }

                        activityAddressSuggestions.value = topSuggestions
                        fullActivityAddresses = fullAddresses
                    }
                }
                else                                   ->
                {
                    // No suggestions
                }

            }
        }
    }

    fun postNewActivity(
        activityTitle : String,
        description : String,
        costPerIndividual : Int,
        costPerGroup : Int,
        groupSize : Int,
        pictures : List<Pair<String, InputStream>>,
        activityDate : EventDate,
        startTime : EventTime,
        endTime : EventTime
    )
    {
        viewModelScope.launch {
            try
            {
                val response = activityRepository.postNewUserActivity(
                    activityTitle,
                    description,
                    costPerIndividual,
                    costPerGroup,
                    groupSize,
                    pictures,
                    activityDate,
                    startTime,
                    endTime,
                    activityAddressSuggestions.value!![0]
                )

                postNewUserActivityResponse.value = PostNewUserActivityResponse(
                    response.isSuccessful,
                    if (response.isSuccessful) "Activity successfully created!" else "Oops, something went wrong"
                )
            }
            catch (e : Exception)
            {
                Log.d(logTag, "activityRepository.postNewUserActivity - exception:\n $e")
                postNewUserActivityResponse.value = PostNewUserActivityResponse(
                    false,
                    "Oops, something went wrong!"
                )
            }
        }
    }
}