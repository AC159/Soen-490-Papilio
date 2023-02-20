package com.soen490chrysalis.papilio.services.network

import com.soen490chrysalis.papilio.BuildConfig
import com.soen490chrysalis.papilio.services.network.requests.ActivitySearchRequest
import com.soen490chrysalis.papilio.services.network.responses.ActivityResponse
import com.soen490chrysalis.papilio.services.network.responses.SearchActivityResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/*
    File that manages the network requests related to activities.

    Author: Anas Peerzada
    Date: January 29th, 2023
 */

private const val BASE_URL = BuildConfig.ACTIVITY_API_URL

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

    Author: Anas Peerzada
    Date: January 29th, 2023
*/
interface IActivityApiService
{
    @GET("getFeeds")
    suspend fun getAllActivities(
        @Query("page") page : String?,
        @Query("size") size : String?
    ) : Response<ActivityResponse>

    @POST("search")
    suspend fun searchActivities(
    @Body keyword : ActivitySearchRequest
    ) : Response<SearchActivityResponse>
}

/*
    DESCRIPTION:
    Singleton object that performs network requests related to Activities

    Author: Anas Peerzada
    Date: January 29th, 2023
*/
object ActivityApi
{
    // Create a singleton object that implements the ActivityApiService interface
    val retrofitService : IActivityApiService by lazy {
        retrofit.create(IActivityApiService::class.java)
    }
}