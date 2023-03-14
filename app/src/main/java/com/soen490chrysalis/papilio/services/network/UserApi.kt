package com.soen490chrysalis.papilio.services.network

import com.soen490chrysalis.papilio.BuildConfig
import com.soen490chrysalis.papilio.services.network.requests.*
import com.soen490chrysalis.papilio.services.network.responses.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/*
    File that manages the network requests related to the user.

    Author: Anastassy Cap
    Date: November 11, 2022
 */

private const val BASE_URL = BuildConfig.USER_API_URL

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
interface IUserApiService {
    @GET("get/{firebaseId}")
    suspend fun getUserByFirebaseId(
        @Path("firebaseId") firebaseId: String?
    ): Response<GetUserByFirebaseIdResponse>

    @GET("get/{id}/activities")
    suspend fun getUserActivities(
        @Path("id") firebaseId: String?
    ): Response<FavoriteActivitiesResponse>

    @GET("get/joinedActivities/{id}")
    suspend fun getUserJoinedActivities(
        @Path("id") firebaseId: String?
    ): Response<JoinedActivitiesResponse>

    @GET("get/{id}/favoriteActivities")
    suspend fun getUserFavoriteActivities(
        @Path("id") id: String?
    ): Response<FavoriteActivitiesResponse>

    @GET("get/isActivityFavorite/{id}/{activityId}")
    suspend fun checkActivityFavorited(
        @Path("id") id: String?,
        @Path("activityId") activityId: String?
    ): Response<CheckFavoriteResponse>

    @POST("createUser")
    suspend fun createUser(
        @Body user: UserRequest
    ): Response<Void>

    @PUT("updateUserProfile")
    suspend fun updateUser(
        @Body user: UserUpdate
    ): Response<Void>

    @PUT("addFavoriteActivity")
    suspend fun addFavoriteActivity(
        @Body body: UserUpdate
    ): Response<FavoriteResponse>

    @PUT("removeFavoriteActivity")
    suspend fun removeFavoriteActivity(
        @Body body: UserUpdate
    ): Response<FavoriteResponse>

    @Multipart
    @POST("addActivity/{firebaseId}")
    @JvmSuppressWildcards
    suspend fun postNewUserActivity(
        @Path("firebaseId") firebaseId : String?,
        @PartMap activity : Map<String, Any>,
        @Part images : List<MultipartBody.Part>
    ) : Response<Void>

    @GET("get-chat-user-token/{firebaseId}")
    suspend fun getUserChatToken(
        @Path("firebaseId") firebaseId : String?
    ) : Response<String>

    @POST("activity/{user_id}/join/{activity_id}")
    suspend fun addUserToActivity(
        @Path("user_id") user_id : String?,
        @Path("activity_id") activity_id : String,
        @Body user_name : AddUserToActivityBody
    ) : Response<Void>

    @DELETE("activity/{user_id}/unjoin/{activity_id}")
    suspend fun removeUserFromActivity(
        @Path("user_id") user_id : String?,
        @Path("activity_id") activity_id : String
    ) : Response<Void>

    @GET("activity/{user_id}/checkJoined/{activity_id}")
    suspend fun checkActivityMember(
        @Path("user_id") user_id : String?,
        @Path("activity_id") activity_id : String
    ) : Response<CheckUserIsMemberOfActivityResponse>
}

/*
    DESCRIPTION:
    Singleton object that performs network requests related to the User.

    Author: Anastassy Cap
    Date: November 1st, 2022
*/
object UserApi {
    // Create a singleton object that implements the UserApiService interface
    val retrofitService: IUserApiService by lazy {
        retrofit.create(IUserApiService::class.java)
    }
}