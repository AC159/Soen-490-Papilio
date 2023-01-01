package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.responses.GetUserByFirebaseIdResponse
import com.soen490chrysalis.papilio.services.network.responses.UserObject
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class UserProfileViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    var userObject : MutableLiveData<GetUserResponse> = MutableLiveData<GetUserResponse>()

    fun getUserByFirebaseId()
    {
        viewModelScope.launch {
            val userResponse = userRepository.getUserByFirebaseId()
            userObject.value = GetUserResponse(true, userResponse?.user)
        }
    }

    fun updateUserProfile(variableMap: Map<String, kotlin.Any>)
    {
        viewModelScope.launch {
            val userResponse = userRepository.updateUser(variableMap)

            val afterUpdateResponse = userRepository.getUserByFirebaseId()

            userObject.value = GetUserResponse(true, afterUpdateResponse?.user)
        }


    }
}