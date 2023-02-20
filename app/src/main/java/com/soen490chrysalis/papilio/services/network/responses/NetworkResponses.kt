package com.soen490chrysalis.papilio.services.network.responses

import android.app.Activity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
    File that will hold all the data classes for response objects

    Author: Anastassy Cap
    Date: November 11, 2022
 */

data class UserObject(
    val firstName : String,
    val lastName : String,
    val email : String,
    @Json(name = "firebase_id")
    val firebaseId : String,
    val countryCode : String?,
    val phone : String?,
    val createdAt : String,
    val updatedAt : String,
    val bio : String
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
    val businessId: String?,
    val userId: String?
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

data class SearchActivityResponse(
    val keyword: String,
    val count: String,
    val rows: List<ActivityObjectLight>
)

// Response object for the /api/user/get/:firebaseId endpoint
data class GetUserByFirebaseIdResponse(
    val found : Boolean,
    val user : UserObject
)
