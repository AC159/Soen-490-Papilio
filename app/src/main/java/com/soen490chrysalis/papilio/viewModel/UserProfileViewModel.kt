package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import kotlinx.coroutines.launch

data class UserProfileRequestResponse(val status: Int, val message: String)

class UserProfileViewModel(private val userRepository: IUserRepository) : ViewModel() {
    var userObject: MutableLiveData<GetUserResponse> = MutableLiveData<GetUserResponse>()
    var passwordChangeResult: MutableLiveData<String> = MutableLiveData<String>()
    private var editedFields: MutableMap<String, Any> = mutableMapOf()
    var updateUserResponse: MutableLiveData<UserProfileRequestResponse> =
        MutableLiveData<UserProfileRequestResponse>()


    fun getUserByFirebaseId() {
        viewModelScope.launch {
            val userResponse = userRepository.getUserByFirebaseId()
            userObject.value = GetUserResponse(true, userResponse?.user)
        }
    }

    fun addEditedField(fieldName: String, fieldValue: Any) {
        editedFields[fieldName] = fieldValue
    }

    fun isEditedFieldsEmpty(): Boolean {
        return editedFields.isEmpty()
    }

    fun updateUserProfile() {
        if (editedFields.isEmpty())
            updateUserResponse.value = UserProfileRequestResponse(0, "No Edited Fields")
        else {
            viewModelScope.launch {
                val userResponse = userRepository.updateUser(editedFields)
                Log.d("userResponse: updateUser ->", userResponse.message())

                updateUserResponse.value =
                    UserProfileRequestResponse(userResponse.code(), userResponse.message())

                val afterUpdateResponse = userRepository.getUserByFirebaseId()

                userObject.value = GetUserResponse(true, afterUpdateResponse?.user)

                editedFields.clear()

            }
        }
    }

    fun changeUserPassword(oldPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        viewModelScope.launch {
            if (user != null) {
                val email = user.email
                val credential = email?.let { EmailAuthProvider.getCredential(it, oldPassword) }

                if (credential != null) {
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { operation ->
                                        if (operation.isSuccessful) {
                                            passwordChangeResult.value =
                                                "Password changed successfully!"
                                            Log.e("updatePassword", "success", operation.exception)
                                        } else {
                                            passwordChangeResult.value =
                                                "Error! Couldn't change password"
                                            Log.e("updatePassword", "error", operation.exception)
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
}