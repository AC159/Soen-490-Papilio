package com.soen490chrysalis.papilio.services.network.responses

import com.squareup.moshi.Json

/*
    File that will hold all the data classes for response objects

    Author: Anastassy Cap
    Date: November 11, 2022
 */

data class UserObject(
    val firstName: String,
    val lastName: String,
    val email: String,
    @Json(name = "firebase_id")
    val firebaseId: String,
    val countryCode: String?,
    val phone: String?,
    val createdAt: String,
    val updatedAt: String,
    val bio: String,
    val image: String
)

data class ActivityObject(
    val id: String?,
    val title: String?,
    val description: String?,
    val costPerIndividual: String?,
    val costPerGroup: String?,
    val groupSize: String?,
    val images: List<String>?,
    val startTime: String?,
    val endTime: String?,
    val address: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val business: BusinessObject?,
    val userId: String?
)

data class BusinessObject(
    val businessId: String?,
    val email: String?
)
data class ActivityObjectLight(
    val id: String?,
    val title: String?,
    val description: String?,
    val images: String?,
)

data class ActivityResponse(
    val count: String,
    val rows: List<ActivityObject>,
    val totalPages: String,
    val currentPage: String
)

data class FavoriteActivitiesResponse(
    val count: String,
    val activities: List<ActivityObject>
)

data class JoinedActivityObject(
    val id: String?,
    val userId: String?,
    val activityId : String?,
    val activity : ActivityObject
)

data class JoinedActivitiesResponse(
    val count: String,
    val row: List<JoinedActivityObject>
)

data class SingleActivityResponse(
    val found: Boolean,
    val activity: ActivityObject
)

data class SearchActivityResponse(
    val keyword: String,
    val count: String,
    val rows: List<ActivityObjectLight>
)

// Response object for the /api/user/get/:firebaseId endpoint
data class GetUserByFirebaseIdResponse(
    val found: Boolean,
    val user: UserObject
)

data class FavoriteUserObject(
    @Json(name = "firebase_id")
    val firebaseId: String?,
    val firstName: String?,
    val lastName: String?,
    val countryCode: String?,
    val phone: String?,
    val email: String?,
    val bio: String?,
    val favoriteActivities: IntArray?
)

data class FavoriteResponse(
    val success: Boolean?,
    val update: FavoriteUserObject?
)

data class CheckFavoriteResponse(
    val isActivityFound: Boolean,
)

data class CheckUserIsMemberOfActivityResponse(
    val joined : Boolean
)