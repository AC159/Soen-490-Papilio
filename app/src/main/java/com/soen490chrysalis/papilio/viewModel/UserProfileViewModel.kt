package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import kotlinx.coroutines.launch

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
            Log.d("userResponse: updateUser ->", userResponse.message())

            val afterUpdateResponse = userRepository.getUserByFirebaseId()

            userObject.value = GetUserResponse(true, afterUpdateResponse?.user)

            editedFields.clear()
        }
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