package com.soen490chrysalis.papilio.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.UserApi

class UserProfileViewModelFactory : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T
    {

        // todo: Here we are creating a new UserRepository instance everytime we want to access it.
        //  Perhaps it should be a singleton that is created only once

        // Initialize Firebase Auth and inject it into the user repository
        val firebaseAuth = FirebaseAuth.getInstance()
        //performance testing for retrofit service
        val myTrace = Firebase.performance.newTrace("user_api_retrofit_service")
        myTrace.start()
        val userRepository : IUserRepository =
            UserRepository(firebaseAuth, userService = UserApi.retrofitService)
        myTrace.stop()
        return UserProfileViewModel(userRepository) as T
    }
}