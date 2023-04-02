package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.services.network.responses.FavoriteActivitiesResponse
import com.soen490chrysalis.papilio.services.network.responses.JoinedActivitiesResponse
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityHistoryViewModel (private val userRepository: UserRepository): ViewModel()
{
    private val logTag = UpcomingActivitiesViewModel::class.java.simpleName
    var activitiesResponse : MutableLiveData<FavoriteActivitiesResponse> =
        MutableLiveData<FavoriteActivitiesResponse>()

    fun getActivityHistory()
    {
        viewModelScope.launch {

            try
            {
                val getFirstActivityResponse = userRepository.getCreatedActivities()
                val firstResponse = FavoriteActivitiesResponse(
                    getFirstActivityResponse.third.count,
                    getFirstActivityResponse.third.activities,
                )
                Log.d(".getCreatedActivities()", firstResponse.toString())

                val getActivityResponse = userRepository.getJoinedActivities()
                val secondResponse = JoinedActivitiesResponse(
                    getActivityResponse.third.count,
                    getActivityResponse.third.row
                )
                Log.d(".getJoinedActivities()", secondResponse.toString())

                val tempList = mutableListOf<ActivityObject>()
                for (activity in secondResponse.row)
                {
                    tempList.add(activity.activity)
                }

                activitiesResponse.value = FavoriteActivitiesResponse(
                    count = (firstResponse.count.toInt() + secondResponse.count.toInt()).toString(),
                    activities = firstResponse.activities.plus(tempList)
                )

                Log.d(logTag, " response from getFirstActivityResponse() --> $getFirstActivityResponse")
                Log.d(logTag, " response from getJoinedActivities() --> $getActivityResponse")
            }
            catch (e : Exception)
            {
                Log.d(logTag, "userRepository.getActivityHistory() - exception:\n $e")
            }

        }
    }
}