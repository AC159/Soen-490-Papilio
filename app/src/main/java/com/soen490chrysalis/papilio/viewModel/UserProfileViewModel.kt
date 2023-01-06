package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class UserProfileViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    var userObject : MutableLiveData<GetUserResponse> = MutableLiveData<GetUserResponse>()
    var passwordChangeResult : MutableLiveData<String> = MutableLiveData<String>()
    private var editedFields : MutableMap<String, kotlin.Any> = mutableMapOf<String, kotlin.Any>()

    fun getUserByFirebaseId()
    {
        viewModelScope.launch {
            val userResponse = userRepository.getUserByFirebaseId()
            userObject.value = GetUserResponse(true, userResponse?.user)
        }
    }

    fun addEditedField(fieldName: String, fieldValue: kotlin.Any)
    {
        editedFields.put(fieldName, fieldValue);
    }

    fun isEditedFieldsEmpty() : Boolean
    {
        return editedFields.isEmpty()
    }

    fun updateUserProfile()
    {
        if(editedFields.isEmpty()) return

        viewModelScope.launch {
            val userResponse = userRepository.updateUser(editedFields)

            val afterUpdateResponse = userRepository.getUserByFirebaseId()

            userObject.value = GetUserResponse(true, afterUpdateResponse?.user)

            editedFields.clear()
        }
    }

    fun validatePhoneNumber(number : String): String?
    {
        if(number.length < 10)
        {
            return "Please enter a valid phone number (10 digits)"
        }

        return null
    }

    fun validateCountryCode(number : String): String?
    {
        if(number.length > 5 || number.isEmpty())
        {
            return "Please enter a valid country code (without the plus sign)"
        }

        return null
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

    fun confirmPassword(password : String, password2 : String) : String?
    {
        if(password == password2)
            return null
        else
            return "Make sure passwords match!"
    }

    fun changeUserPassword(oldPassword : String, newPassword : String)
    {
        viewModelScope.launch {

            val user = FirebaseAuth.getInstance().currentUser
            val credential = user!!.email?.let { EmailAuthProvider.getCredential(it, oldPassword) }

            if (credential != null) {
                user!!.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    passwordChangeResult.value = "Password changed successfully!"
                                    Log.e("updatePassword", "success", task.exception)
                                } else {
                                    passwordChangeResult.value = "Error! Couldn't change password"
                                    Log.e("updatePassword", "error", task.exception)
                                }
                            }
                        } else {
                            passwordChangeResult.value = "Error! Couldn't authenticate user"
                            Log.e("updatePassword", "error", task.exception)
                        }
                    }
            }


        }

    }
}