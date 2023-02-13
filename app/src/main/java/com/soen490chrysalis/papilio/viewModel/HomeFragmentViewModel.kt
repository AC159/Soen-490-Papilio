package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.ActivityResponse
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val activityRepository: IActivityRepository) : ViewModel() {

    var activityResponse: MutableLiveData<ActivityResponse> = MutableLiveData<ActivityResponse>()

    fun getAllActivities(page: String, size: String)
    {
        viewModelScope.launch {
            val getAllActivitiesResponse = activityRepository.getAllActivities(page, size)
            activityResponse.value = ActivityResponse(
                getAllActivitiesResponse.body()!!.count,
                getAllActivitiesResponse.body()!!.rows,
                getAllActivitiesResponse.body()!!.totalPages,
                getAllActivitiesResponse.body()!!.currentPage
            )
            Log.d("getAllAct", activityResponse.toString())
        }
    }

}