package com.soen490chrysalis.papilio.viewModel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.activities.ActivityRepository
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.UserApi
import com.soen490chrysalis.papilio.viewModel.CreateActivityViewModel

class CreateActivityViewModelFactory : ViewModelProvider.NewInstanceFactory()
{
    @Override
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T
    {
        // Initialize Firebase Auth and inject it into the user repository
        val firebaseAuth = FirebaseAuth.getInstance()
        val activityRepository : IActivityRepository =
            ActivityRepository(firebaseAuth, userAPIService = UserApi.retrofitService)

        return CreateActivityViewModel(activityRepository) as T
    }
}