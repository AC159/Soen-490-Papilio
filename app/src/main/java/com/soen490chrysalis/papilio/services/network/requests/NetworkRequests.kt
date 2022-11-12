package com.soen490chrysalis.papilio.services.network.requests

import com.squareup.moshi.Json

/*
    File that will hold all the data classes for request objects

    Author: Anastassy Cap
    Date: November 11, 2022
 */

data class User(
    val firstName : String,
    val lastName : String,
    val email : String,
    @Json(name = "firebase_id")
    val firebaseId : String
)

data class UserRequest(
    @Json(name = "user")
    val user : User
)

