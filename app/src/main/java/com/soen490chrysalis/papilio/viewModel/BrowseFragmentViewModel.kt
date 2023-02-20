package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.ActivityResponse
import com.soen490chrysalis.papilio.services.network.responses.SearchActivityResponse
import kotlinx.coroutines.launch

class BrowseFragmentViewModel(private val activityRepository: IActivityRepository) : ViewModel() {

    private val logTag = BrowseFragmentViewModel::class.java.simpleName
    var activityResponse: MutableLiveData<SearchActivityResponse> = MutableLiveData<SearchActivityResponse>()

    fun searchActivities(query: String) {
        viewModelScope.launch {
            try {
                val getAllActivitiesResponse = activityRepository.searchActivities(query)
                activityResponse.value = SearchActivityResponse(
                    getAllActivitiesResponse.body()!!.keyword,
                    getAllActivitiesResponse.body()!!.count,
                    getAllActivitiesResponse.body()!!.rows
                )
                Log.d("searchActivities", activityResponse.value.toString())
            } catch (e: Exception) {
                Log.d(logTag, "activityRepository.getAllActivities - exception:\n $e")
            }
        }
    }

}