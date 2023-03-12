package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import kotlinx.coroutines.launch

data class APIResponse(var isSuccess : Boolean, var errorMessage : String)
data class CheckActivityMember(
    var isSuccess : Boolean,
    var errorMessage : String,
    var hasUserJoined : Boolean
)

class ActivityInfoViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    private val logTag = ActivityInfoViewModel::class.java.simpleName
    var checkActivityMemberResponse = MutableLiveData<CheckActivityMember>()
    var jonActivityResponse = MutableLiveData<APIResponse>()
    var leaveActivityResponse = MutableLiveData<APIResponse>()

    // Function that fetches information about whether or not a user has joined an activity
    fun checkActivityMember(activity_id : String)
    {
        viewModelScope.launch {
            val result = userRepository.checkActivityMember(activity_id)
            Log.d(logTag, "response from checkActivityMember(): $result")
            checkActivityMemberResponse.value =
                CheckActivityMember(result.first, result.second, result.third)
        }
    }

    fun joinActivity(activity_id : String)
    {
        viewModelScope.launch {
            val result = userRepository.addUserToActivity(activity_id)
            Log.d(logTag, "response from joinActivity(): $result")
            jonActivityResponse.value = APIResponse(result.first, result.second)
        }
    }

    fun leaveActivity(activity_id : String)
    {
        viewModelScope.launch {
            val result = userRepository.removeUserFromActivity(activity_id)
            Log.d(logTag, "response from leaveActivity(): $result")
            leaveActivityResponse.value = APIResponse(result.first, result.second)
        }
    }
}