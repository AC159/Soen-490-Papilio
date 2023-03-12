package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.responses.CheckFavoriteResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteActivitiesResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteResponse
import com.soen490chrysalis.papilio.services.network.responses.SingleActivityResponse
import kotlinx.coroutines.launch

class DisplayActivityViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private val logTag = DisplayActivityViewModel::class.java.simpleName
    var activitiesResponse: MutableLiveData<FavoriteActivitiesResponse> =
        MutableLiveData<FavoriteActivitiesResponse>()
    var checkActivityFavoritedResponse: MutableLiveData<CheckFavoriteResponse> =
        MutableLiveData<CheckFavoriteResponse>()


    fun getFavoriteActivities() {
        viewModelScope.launch {
            try {
                val getAllActivitiesResponse = userRepository.getFavoriteActivities()
                activitiesResponse.value = FavoriteActivitiesResponse(
                    getAllActivitiesResponse.body()!!.count,
                    getAllActivitiesResponse.body()!!.activities
                )
                Log.d("getFavoriteActivities", activitiesResponse.value.toString())
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.getFavoriteActivities - exception:\n $e")
            }
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
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.addFavoriteActivity - exception:\n $e")
            }
        }
    }

    fun removeFavoriteActivity(activityId: Number) {
        viewModelScope.launch {
            try {
                val getActivityResponse = userRepository.removeFavoriteActivity(activityId)

                Log.d("addFavoriteActivity", getActivityResponse.message())
            } catch (e: Exception) {
                Log.d(logTag, "userRepository.addFavoriteActivity - exception:\n $e")
            }
        }
    }

}