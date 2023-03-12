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

class FavoriteActivitiesViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private val logTag = FavoriteActivitiesViewModel::class.java.simpleName
    var activitiesResponse: MutableLiveData<FavoriteActivitiesResponse> =
        MutableLiveData<FavoriteActivitiesResponse>()

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
}