package com.soen490chrysalis.papilio.repository.mocks

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.responses.*
import org.mockito.Mockito
import retrofit2.Response
import java.io.InputStream

/*
    DESCRIPTION:
    Mock user repository class for unit tests

    Author: Anastassy Cap
    Date: October 5, 2022
*/
class MockUserRepository : IUserRepository
{
    private var firebaseUserMock = Mockito.mock(FirebaseUser::class.java)

    override fun initialize(googleSignInClient : GoogleSignInClient)
    {
    }

    override fun getUser() : FirebaseUser?
    {
        return firebaseUserMock
    }

    override suspend fun getUserByFirebaseId() : GetUserByFirebaseIdResponse?
    {
        val user = UserObject(
            "firstName",
            "lastName",
            "validEmail@gmail.com",
            "faoeijga;eosigj",
            null,
            null,
            "November 13 2022",
            "November 13 2022",
            "Hello! It's me, firstName!",
            "ewfj13498to3ifj0193rfg93rtg"
        )
        return GetUserByFirebaseIdResponse(true, user)
    }

    override suspend fun createUser(
        user : FirebaseUser?
    ) : Response<Void>
    {
        return Response.success(null)
    }

    override suspend fun isActivityFavorited(activityId : String?) : Triple<Boolean, String, CheckFavoriteResponse>
    {
        return Triple(true, "", CheckFavoriteResponse(false))
    }

    override suspend fun addFavoriteActivity(activityId : Number) : Triple<Boolean, String, FavoriteResponse>
    {
        return Triple(true, "", FavoriteResponse(true, null))
    }

    override suspend fun removeFavoriteActivity(activityId : Number) : Triple<Boolean, String, FavoriteResponse>
    {
        return Triple(true, "", FavoriteResponse(true, null))
    }

    override suspend fun getFavoriteActivities() : Triple<Boolean, String, FavoriteActivitiesResponse>
    {
        return Triple(true, "", FavoriteActivitiesResponse("1", listOf<ActivityObject>()))
    }

    override suspend fun getJoinedActivities() : Triple<Boolean, String, JoinedActivitiesResponse>
    {
        return Triple(true, "", JoinedActivitiesResponse("1", listOf<JoinedActivityObject>()))
    }

    override suspend fun getCreatedActivities() : Triple<Boolean, String, FavoriteActivitiesResponse>
    {
        return Triple(true, "", FavoriteActivitiesResponse("1", listOf<ActivityObject>()))
    }

    override suspend fun updateUser(
        variableMap : Map<String, Any>
    ) : Response<Void>
    {
        return Response.success(null)
    }

    override suspend fun updateUserProfilePic(
        image : Pair<String, InputStream>
    ) : Response<Void>
    {
        return Response.success(null)
    }

    override suspend fun firebaseAuthWithGoogle(idToken : String) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName : String,
        lastName : String,
        emailAddress : String,
        password : String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseLoginWithEmailAndPassword(
        emailAddress : String,
        password : String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun getNewChatTokenForUser(firebaseId : String) : String?
    {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSnNNVlhnVDJNaE44eGpwVzFOTnZBTXFMQURmMSJ9.N-FhnRWLgkGP6knf_QD7gWgUJ7Fm4wtbKkodAUqSlwU"
    }

    override suspend fun addUserToActivity(
        activity_id : String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun removeUserFromActivity(activity_id : String) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun checkActivityMember(activity_id : String) : Triple<Boolean, String, Boolean>
    {
        return Triple(true, "", true)
    }
}