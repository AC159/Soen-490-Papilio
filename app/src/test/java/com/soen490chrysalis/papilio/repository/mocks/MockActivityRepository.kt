package com.soen490chrysalis.papilio.repository.mocks

import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.services.network.responses.ActivityResponse
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import retrofit2.Response
import java.io.InputStream

class MockActivityRepository : IActivityRepository {
    override suspend fun postNewUserActivity(
        activityTitle: String,
        description: String,
        costPerIndividualCost: Int,
        costPerGroupCost: Int,
        groupSize: Int,
        pictures: List<Pair<String, InputStream>>,
        activityDate: EventDate,
        startTime: EventTime,
        endTime: EventTime,
        activityAddress: String
    ): Response<Void> {
        return Response.success(null)
    }

    override suspend fun getAllActivities(
        page: String,
        size: String
    ): Response<ActivityResponse> {

        var activityList: MutableList<ActivityObject> = mutableListOf()
        for (i in 1..size.toInt()) {
            val activityObject = ActivityObject(
                i.toString(),
                "Activity $i Title",
                "This is Activity $i",
                "0",
                "0",
                "4",
                listOf("a" + i + "image1", "a" + i + "image1"),
                "1500",
                "2000",
                "Activity $i Address",
                "A$i Creation Time",
                "A$i Update Time",
                null,
                "user $i"
            )

            activityList.add(activityObject)
        }

        val activityResponse = ActivityResponse(
            activityList.size.toString(),
            activityList,
            size,
            page,
        )

        return Response.success(activityResponse)
    }
}