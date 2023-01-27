package com.soen490chrysalis.papilio.repository.mocks

import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import retrofit2.Response
import java.io.InputStream

class MockActivityRepository : IActivityRepository
{
    override suspend fun postNewUserActivity(
        activityTitle : String,
        description : String,
        groupSize : Int,
        pictures : List<Pair<String, InputStream>>,
        activityDate : EventDate,
        startTime : EventTime,
        endTime : EventTime,
        activityAddress : String
    ) : Response<Void>
    {
        return Response.success(null)
    }
}