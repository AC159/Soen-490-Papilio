package com.soen490chrysalis.papilio.services.network.responses

import com.squareup.moshi.Json

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

// Response object for the /api/user/get/:firebaseId endpoint
data class GetUserByFirebaseIdResponse(
    val found : Boolean,
    val user : UserObject
)

data class GenreObject(
    val id : String?,
    val name : String?,
    val url : String?,
    val category : String?,
    val createdAt : String?,
    val updatedAt : String?
)

data class GenreObjectResponse(
    val rows: List<GenreObject>?
)