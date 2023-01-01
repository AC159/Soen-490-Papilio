package com.soen490chrysalis.papilio.services.network

import com.soen490chrysalis.papilio.BuildConfig
import com.soen490chrysalis.papilio.services.network.requests.UserRequest
import com.soen490chrysalis.papilio.services.network.requests.UserUpdate
import com.soen490chrysalis.papilio.services.network.responses.GetUserByFirebaseIdResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/*
    File that manages the network requests related to the user.

    Author: Anastassy Cap
    Date: November 11, 2022
 */

private const val BASE_URL = BuildConfig.BACKEND_API_URL

// Build the Moshi object with Kotlin adapter factory that Retrofit will be using
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// The Retrofit object with the Moshi converter.
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

/*
    DESCRIPTION:
    Interface that instructs retrofit how to communicate with the server to make network requests.
    This is where new endpoints will be added in the future.

    Author: Anastassy Cap
    Date: November 1st, 2022
*/
interface IUserApiService
{
    @GET("get/{firebaseId}")
    suspend fun getUserByFirebaseId(
        @Path("firebaseId") firebaseId : String?
    ) : Response<GetUserByFirebaseIdResponse>

    @POST("createUser")
    suspend fun createUser(
        @Body user : UserRequest
    ) : Response<Void>

    @PUT("updateUserProfile")
    suspend fun updateUser(
        @Body user : UserUpdate,

    ) : Response<Void>
}

/*
    DESCRIPTION:
    Singleton object that performs network requests related to the User.

    Author: Anastassy Cap
    Date: November 1st, 2022
*/
object UserApi
{
    // Create a singleton object that implements the UserApiService interface
    val retrofitService : IUserApiService by lazy {
        retrofit.create(IUserApiService::class.java)
    }
}