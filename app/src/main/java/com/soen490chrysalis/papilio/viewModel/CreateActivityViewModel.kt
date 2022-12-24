package com.soen490chrysalis.papilio.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autofill.*
import com.soen490chrysalis.papilio.BuildConfig
import kotlinx.coroutines.launch

class CreateActivityViewModel : ViewModel()
{
    private val logTag = CreateActivityViewModel::class.java.simpleName
    var activityAddressSuggestions : MutableLiveData<List<String>> =
        MutableLiveData<List<String>>(ArrayList())

    /* We keep track of whole mapbox address suggestions because they contain lat/long coordinates
       which will be useful when we want to display the activity location on a map
    */
    private var fullActivityAddresses : List<AddressAutofillSuggestion> = ArrayList()

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

    fun validateActivityPictureUris(pictures : List<Uri>) : String?
    {
        if (pictures.isNotEmpty()) return null
        return "Don't forget to add some pictures!"
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
}