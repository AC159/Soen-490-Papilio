package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.Genre.IGenreRepository
import com.soen490chrysalis.papilio.services.network.responses.GenreObjectResponse
import kotlinx.coroutines.launch

class GenreViewModel(private val genreRepository: IGenreRepository) : ViewModel() {
    var genreObject: MutableLiveData<GenreObjectResponse> = MutableLiveData<GenreObjectResponse>()

    fun getAllGenres(category : String?){
           viewModelScope.launch {
//               genreObject.value = genreRepository.getAllGenres(category).body()
               Log.d("getAllGenres", genreRepository.getAllGenres(category).body().toString())
           }
    }
}