package com.soen490chrysalis.papilio.viewModel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.activities.ActivityRepository
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.ActivityApi
import com.soen490chrysalis.papilio.services.network.UserApi
import com.soen490chrysalis.papilio.viewModel.ActivityInfoViewModel

class ActivityInfoViewModelFactory : ViewModelProvider.NewInstanceFactory()
{
    @Override
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T
    {
        // Initialize Firebase Auth and inject it into the user repository
        val firebaseAuth = FirebaseAuth.getInstance()
        val userRepository : IUserRepository =
            UserRepository(firebaseAuth, userService = UserApi.retrofitService)

        val activityRepository : IActivityRepository =
            ActivityRepository(firebaseAuth, UserApi.retrofitService, ActivityApi.retrofitService)

        return ActivityInfoViewModel(userRepository, activityRepository) as T
    }
}