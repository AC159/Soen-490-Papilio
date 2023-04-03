package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.genre.IGenreRepository
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.requests.SubmitQuiz
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import kotlinx.coroutines.launch

class GenreViewModel(
    private val genreRepository : IGenreRepository,
    private val userRepository : IUserRepository
) : ViewModel()
{
    private val logTag = GenreViewModel::class.java.simpleName
    var genreObject : MutableLiveData<List<GenreObject>> = MutableLiveData<List<GenreObject>>()
    var submitQuizResponse : MutableLiveData<Pair<Int, String>> =
        MutableLiveData<Pair<Int, String>>()

    fun getAllGenres()
    {
        viewModelScope.launch {
            genreObject.value = genreRepository.getAllGenres().third
            Log.d(logTag, "Received genres from the repository:\n ${genreObject.value.toString()}")
        }
    }

    fun submitQuiz(indoor : Boolean, outdoor : Boolean, genres : IntArray)
    {
        viewModelScope.launch {
            submitQuizResponse.value =
                userRepository.submitQuiz(SubmitQuiz(indoor, outdoor, genres))
            Log.d(logTag, "Submit Quiz Response -> :\n ${submitQuizResponse.value.toString()}")
        }
    }
}