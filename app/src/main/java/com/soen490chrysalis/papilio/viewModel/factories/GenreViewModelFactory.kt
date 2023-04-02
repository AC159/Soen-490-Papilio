package com.soen490chrysalis.papilio.viewModel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.genre.GenreRepository
import com.soen490chrysalis.papilio.repository.genre.IGenreRepository
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.GenreApi
import com.soen490chrysalis.papilio.services.network.UserApi
import com.soen490chrysalis.papilio.viewModel.GenreViewModel

class GenreViewModelFactory : ViewModelProvider.NewInstanceFactory()
{
    @Override
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T
    {
        // Initialize Firebase Auth and inject it into the user repository
        val firebaseAuth = FirebaseAuth.getInstance()

        val genreRepository : IGenreRepository =
            GenreRepository(GenreApi.retrofitService)

        val userRepository : IUserRepository =
            UserRepository(firebaseAuth, userService = UserApi.retrofitService)

        return GenreViewModel(genreRepository, userRepository) as T
    }
}