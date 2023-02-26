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
    var activitiesResponse: MutableLiveData<SearchActivityResponse> = MutableLiveData<SearchActivityResponse>()
    var activityResponse: MutableLiveData<SingleActivityResponse> = MutableLiveData<SingleActivityResponse>()

    fun searchActivities(query: String) {
        viewModelScope.launch {
            try {
                val getAllActivitiesResponse = activityRepository.searchActivities(query)
                activitiesResponse.value = SearchActivityResponse(
                    getAllActivitiesResponse.body()!!.keyword,
                    getAllActivitiesResponse.body()!!.count,
                    getAllActivitiesResponse.body()!!.rows
                )
                Log.d("searchActivities", activitiesResponse.value.toString())
            } catch (e: Exception) {
                Log.d(logTag, "activityRepository.getAllActivities - exception:\n $e")
            }
        }
    }

    fun getActivity(activityId : Number)
    {
        viewModelScope.launch {
            try {
                val getActivityResponse = activityRepository.getActivity(activityId)
                activityResponse.value = SingleActivityResponse(
                    getActivityResponse.body()!!.found,
                    getActivityResponse.body()!!.activity,
                )
                Log.d("getActivity", activitiesResponse.value.toString())
            } catch (e: Exception) {
                Log.d(logTag, "activityRepository.getActivity - exception:\n $e")
            }
        }
    }

}