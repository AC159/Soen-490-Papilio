package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.SearchActivityResponse
import com.soen490chrysalis.papilio.services.network.responses.SingleActivityResponse
import kotlinx.coroutines.launch

class BrowseFragmentViewModel(private val activityRepository: IActivityRepository) : ViewModel() {

    private val logTag = BrowseFragmentViewModel::class.java.simpleName
    var activitiesResponse: MutableLiveData<SearchActivityResponse> =
        MutableLiveData<SearchActivityResponse>()
    var activityResponse: MutableLiveData<SingleActivityResponse> =
        MutableLiveData<SingleActivityResponse>()

    fun searchActivities(query: String) {
        viewModelScope.launch {
            try {
                val getAllActivitiesResponse = activityRepository.searchActivities(query)
                activitiesResponse.value = SearchActivityResponse(
                    getAllActivitiesResponse.third.keyword,
                    getAllActivitiesResponse.third.count,
                    getAllActivitiesResponse.third.rows
                )
                Log.d(logTag, "response from searchActivities() --> $getAllActivitiesResponse")
            } catch (e: Exception) {
                Log.d(logTag, "activityRepository.searchActivities - exception:\n $e")
            }
        }
    }

    fun getActivity(activityId: Number) {
        viewModelScope.launch {
            try {
                val getActivityResponse = activityRepository.getActivity(activityId)
                activityResponse.value = SingleActivityResponse(
                    getActivityResponse.third.found,
                    getActivityResponse.third.activity,
                )
                Log.d(logTag, "response from getActivity --> $getActivityResponse")
            } catch (e: Exception) {
                Log.d(logTag, "activityRepository.getActivity - exception:\n $e")
            }
        }
    }

}