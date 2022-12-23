package com.soen490chrysalis.papilio.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel

class CreateActivityViewModel : ViewModel()
{
    private val logTag = CreateActivityViewModel::class.java.simpleName

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
}