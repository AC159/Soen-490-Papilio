package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.genre.IGenreRepository
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import kotlinx.coroutines.launch

class GenreViewModel(private val genreRepository : IGenreRepository) : ViewModel()
{
    private val logTag = GenreViewModel::class.java.simpleName
    var genreObject : MutableLiveData<List<GenreObject>> = MutableLiveData<List<GenreObject>>()

    fun getAllGenres()
    {
        viewModelScope.launch {
            genreObject.value = genreRepository.getAllGenres().body()
            Log.d(logTag, "Received genres from the repository:\n ${genreObject.value.toString()}")
        }
    }
}