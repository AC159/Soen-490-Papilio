package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository

/*
    DESCRIPTION:
    Class that handles the logic related to the data of the corresponding view (i.e. activity).
    It is important to pass a repository that implements an interface such as IUserRepository
    to allow for dependency injection in our unit tests.

    Author: Anastassy Cap
    Date: October 5, 2022
*/
class LoginViewModel(private val userRepository: IUserRepository) : ViewModel()
{
    var loginSuccessful : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun initialize(googleSignInClient : GoogleSignInClient)
    {
        userRepository.initialize(googleSignInClient)
    }

    fun getUser() : FirebaseUser?
    {
        return userRepository.getUser()
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        userRepository.firebaseAuthWithGoogle(idToken) { authResult: Boolean ->
            /* This will trigger the observer in the LoginActivity which will redirect the
            user to a new page */
            loginSuccessful.value = authResult
            Log.d(Log.DEBUG.toString(), "Return value from userRepository $authResult")
        }
    }

}