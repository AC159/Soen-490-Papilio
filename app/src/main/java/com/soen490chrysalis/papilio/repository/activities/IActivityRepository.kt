package com.soen490chrysalis.papilio.repository.activities

import com.soen490chrysalis.papilio.services.network.responses.ActivityResponse
import com.soen490chrysalis.papilio.services.network.responses.SearchActivityResponse
import com.soen490chrysalis.papilio.services.network.responses.SingleActivityResponse
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import retrofit2.Response
import java.io.InputStream

/*
    DESCRIPTION:
    Interface that declares all the methods related to user activities

    Author: Anastassy Cap
    Date: January 9 2022
*/
interface IActivityRepository
{
    suspend fun postNewUserActivity(
        activityTitle : String,
        description : String,
        costPerIndividual: Int,
        costPerGroup: Int,
        groupSize : Int,
        pictures : List<Pair<String, InputStream>>,
        activityDate : EventDate,
        startTime : EventTime,
        endTime : EventTime,
        activityAddress : String
    ) : Response<Void>

    suspend fun getAllActivities(
        page: String,
        size: String
    ) : Response<ActivityResponse>

    suspend fun getActivity(
        activityId: Number
    ) : Response<SingleActivityResponse>

    suspend fun searchActivities(
        query: String
    ) : Response<SearchActivityResponse>
}