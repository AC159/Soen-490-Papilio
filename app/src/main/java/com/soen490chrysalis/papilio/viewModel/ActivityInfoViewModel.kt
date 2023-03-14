package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.responses.CheckFavoriteResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteActivitiesResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteResponse
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
    var activitiesResponse: MutableLiveData<FavoriteActivitiesResponse> =
        MutableLiveData<FavoriteActivitiesResponse>()
    var checkActivityFavoritedResponse: MutableLiveData<CheckFavoriteResponse> =
        MutableLiveData<CheckFavoriteResponse>()
    var activityFavoritedResponse: MutableLiveData<FavoriteResponse> =
        MutableLiveData<FavoriteResponse>()

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

    fun checkActivityFavorited(activityId: Number) {
        viewModelScope.launch {
            try {
                val getActivityResponse = userRepository.isActivityFavorited(activityId.toString())
                checkActivityFavoritedResponse.value = CheckFavoriteResponse(
                    getActivityResponse.body()!!.isActivityFound
                )
                Log.d("checkActivityFavorited", activitiesResponse.value.toString())
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.checkActivityFavorited - exception:\n $e")
            }
        }
    }

    fun addFavoriteActivity(activityId: Number) {
        viewModelScope.launch {
            try {
                val getActivityResponse = userRepository.addFavoriteActivity(activityId)

                Log.d("addFavoriteActivity", getActivityResponse.message())

                activityFavoritedResponse.value = FavoriteResponse(
                    getActivityResponse.body()?.success,
                    getActivityResponse.body()?.update
                )
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.addFavoriteActivity - exception:\n $e")
            }
        }
    }

    fun removeFavoriteActivity(activityId: Number) {
        viewModelScope.launch {
            try {
                val getActivityResponse = userRepository.removeFavoriteActivity(activityId)

                Log.d("removeFavoriteActivity", getActivityResponse.message())

                activityFavoritedResponse.value = FavoriteResponse(
                    getActivityResponse.body()?.success,
                    getActivityResponse.body()?.update
                )
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.removeFavoriteActivity - exception:\n $e")
            }
        }
    }
}