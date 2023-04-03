package com.soen490chrysalis.papilio.repository.mocks

import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.*
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import retrofit2.Response
import java.io.InputStream

class MockActivityRepository : IActivityRepository {
    private val simpleUser = SimpleUserObject("somefirebaseid", "validEmail@gmail.com")

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
    ): Triple<Boolean, String, ActivityResponse> {

        val activityList: MutableList<ActivityObject> = mutableListOf()
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
                false,
                "A$i Creation Time",
                "A$i Update Time",
                null,
                simpleUser
            )

            activityList.add(activityObject)
        }

        val activityResponse = ActivityResponse(
            activityList.size.toString(),
            activityList,
            size,
            page,
        )

        return Triple(true, "", activityResponse)
    }

    override suspend fun getActivity(activityId: Number): Triple<Boolean, String, SingleActivityResponse> {

            val activityObject = ActivityObject(
                activityId.toString(),
                "Activity 1 Title",
                "This is Activity 1",
                "0",
                "0",
                "4",
                listOf("a" + 1 + "image1", "a" + 2 + "image1"),
                "1500",
                "2000",
                "Activity 1 Address",
                false,
                "A1 Creation Time",
                "A1 Update Time",
                null,
                simpleUser
            )


        val activityResponse = SingleActivityResponse(
            true,
            activityObject,
        )

        return Triple(true, "", activityResponse)
    }

    override suspend fun searchActivities(query: String): Triple<Boolean, String, SearchActivityResponse> {

        val activityList: MutableList<ActivityObjectLight> = mutableListOf()
        for (i in 1..5) {
            val activityObject = ActivityObjectLight(
                i.toString(),
                "Activity $i Title",
                "This is Activity $i",
                "a" + i + "image1",
            )

            activityList.add(activityObject)
        }

        val activityResponse = SearchActivityResponse(
            query,
            activityList.count().toString(),
            activityList
        )

        return Triple(true, "", activityResponse)

    }

    override suspend fun open(activityId : Number) : Pair<Int, String>
    {
        return Pair(200, "OK")
    }

    override suspend fun close(activityId : Number) : Pair<Int, String>
    {
        return Pair(200, "OK")
    }
}