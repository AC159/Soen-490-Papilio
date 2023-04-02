package com.soen490chrysalis.papilio.services.network.requests

import com.squareup.moshi.Json

/*
    File that will hold all the data classes for request objects

    Author: Anastassy Cap
    Date: November 11, 2022
 */

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    @Json(name = "firebase_id")
    val firebaseId: String
)

data class UserRequest(
    @Json(name = "user")
    val user: User
)

data class ActivitySearchRequest(
    @Json(name = "keyword")
    val keyword: String
)

/*
    Data classes used to update user personal information
 */
data class Identifier(
    @Json(name = "firebase_id")
    val firebaseId: String
)

data class UserUpdate(
    @Json(name = "identifier")
    val identifier: Identifier,

    @Json(name = "update")
    val update: Map<String, Any>
)

// Request bodies to add/remove a user from an activity chat
data class AddUserToActivityBody(
    val user_name : String?
)

data class SubmitQuiz(
    @Json(name = "indoor")
    val indoor : Boolean,

    @Json(name = "outdoor")
    val outdoor : Boolean,

    @Json(name = "genres")
    val genres : IntArray
)
