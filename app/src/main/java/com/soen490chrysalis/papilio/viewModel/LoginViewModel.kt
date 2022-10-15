package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import java.util.regex.Pattern

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
    var signUpSuccessful : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun initialize(googleSignInClient : GoogleSignInClient)
    {
        userRepository.initialize(googleSignInClient)
    }

    fun getUser() : FirebaseUser?
    {
        return userRepository.getUser()
    }

    fun validateFirstName( firstName : String ) : String?
    {
        if (firstName.length in 1..25)
        {
            return null
        }
        return "First name must be between 1 and 25 characters long!"
    }

    fun validateLastName( lastName : String ) : String?
    {
        if (lastName.length in 1..25)
        {
            return null
        }
        return "Last name must be between 1 and 25 characters long!"
    }

    fun validateEmailAddress( emailAddress : String ) : String?
    {
        if ( emailAddress.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() )
        {
            return null
        }
        return "Not a valid email!"
    }

    fun validatePassword( password : String ) : String?
    {
        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         // at least 1 digit
                "(?=.*[a-z])" +         // at least 1 lower case letter
                "(?=.*[A-Z])" +         // at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      // any letter
                "(?=.*[@#$%^&+=])" +    // at least 1 special character
                "(?=\\S+$)" +           // no white spaces
                ".{6,}" +               // at least 6 characters
                "$")

        if ( password.length in 6..20 && passwordREGEX.matcher(password).matches() )
        {
            return null
        }
        return "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        userRepository.firebaseAuthWithGoogle(idToken) { authResult: Boolean ->
            /* This will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
            signUpSuccessful.value = authResult
            Log.d(Log.DEBUG.toString(), "Return value from userRepository $authResult")
        }
    }

    fun firebaseCreateAccountWithEmailAndPassword( emailAddress: String, password: String )
    {
        userRepository.firebaseCreateAccountWithEmailAndPassword(emailAddress, password) { authResult : Boolean ->
            /* This will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
            signUpSuccessful.value = authResult
            Log.d(Log.DEBUG.toString(), "Return value from userRepository $authResult")
        }
    }

    fun firebaseLoginWithEmailAndPassword( emailAddress: String, password: String )
    {
        userRepository.firebaseLoginWithEmailAndPassword(emailAddress, password) { authResult : Boolean ->
            /* This will trigger the observer in the activity that listens to changes which will redirect the
            user to a new page */
            signUpSuccessful.value = authResult
            Log.d(Log.DEBUG.toString(), "Return value from userRepository $authResult")
        }
    }
}