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

/*
    DESCRIPTION:
    Class that handles the logic related to the data of the corresponding view (i.e. activity).
    It is important to pass a repository that implements an interface such as IUserRepository
    to allow for dependency injection in our unit tests.

    Author: Anastassy Cap
    Date: October 5, 2022
*/
data class AuthResponse(var authSuccessful : Boolean, var errorMessage : String)
data class GetUserResponse(var requestIsFinished : Boolean, var userObject : UserObject?)

class LoginViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    private val logTag = LoginViewModel::class.java.simpleName
    var authResponse : MutableLiveData<AuthResponse> = MutableLiveData<AuthResponse>()
    var userObject : MutableLiveData<GetUserResponse> = MutableLiveData<GetUserResponse>()

    fun initialize(googleSignInClient : GoogleSignInClient)
    {
        userRepository.initialize(googleSignInClient)
    }

    fun getUser() : FirebaseUser?
    {
        return userRepository.getUser()
    }

    fun getUserByFirebaseId()
    {
        viewModelScope.launch {
            val userResponse = userRepository.getUserByFirebaseId()
            userObject.value = GetUserResponse(true, userResponse?.user)
        }
    }

    fun validateFirstName(firstName : String) : String?
    {
        if (firstName.length in 1..25)
        {
            return null
        }
        return "First name must be between 1 and 25 characters long!"
    }

    fun validateLastName(lastName : String) : String?
    {
        if (lastName.length in 1..25)
        {
            return null
        }
        return "Last name must be between 1 and 25 characters long!"
    }

    fun validateEmailAddress(emailAddress : String) : String?
    {
        if (emailAddress.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(emailAddress)
                        .matches()
        )
        {
            return null
        }
        return "Not a valid email!"
    }

    fun validatePassword(password : String) : String?
    {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         // at least 1 digit
                    "(?=.*[a-z])" +         // at least 1 lower case letter
                    "(?=.*[A-Z])" +         // at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      // any letter
                    "(?=.*[!@#$%^&*()_+])" +// at least 1 special character
                    "(?=\\S+$)" +           // no white spaces
                    ".{6,}" +               // at least 6 characters
                    "$"
        )

        if (password.length in 6..20 && passwordREGEX.matcher(password).matches())
        {
            return null
        }
        return "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
    }

    fun handleAuthResult(authResult : Boolean, errorMessage : String)
    {
        if (!authResult)
        {
            // authentication was not successful, so we set an error message
            authResponse.value = AuthResponse(false, errorMessage)
        }
        else
        {
            authResponse.value = AuthResponse(true, errorMessage)
        }
        Log.d(logTag, "Return value from userRepository $authResult")
    }

    fun firebaseAuthWithGoogle(idToken : String)
    {
        /* Calling the handleAuthResult function will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
        // create a coroutine that will run in the UI thread
        viewModelScope.launch {
            val result = userRepository.firebaseAuthWithGoogle(idToken)
            handleAuthResult(result.first, result.second)
        }
    }

    fun firebaseCreateAccountWithEmailAndPassword(
        firstName : String,
        lastName : String,
        emailAddress : String,
        password : String
    )
    {
        /* Calling the handleAuthResult function will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
        // create a coroutine that will run in the UI thread
        viewModelScope.launch {
            val result = userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName.trim(),
                lastName.trim(),
                emailAddress,
                password
            )
            handleAuthResult(result.first, result.second)
        }
    }

    fun firebaseLoginWithEmailAndPassword(emailAddress : String, password : String)
    {
        /* Calling the handleAuthResult function will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
        // create a coroutine that will run in the UI thread
        viewModelScope.launch {
            val result = userRepository.firebaseLoginWithEmailAndPassword(emailAddress, password)
            handleAuthResult(result.first, result.second)
        }
    }
}