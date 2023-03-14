package com.soen490chrysalis.papilio.repository.mocks

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.services.network.responses.*
import org.mockito.Mockito
import retrofit2.Response

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
            "Hello! It's me, firstName!"
        )
        return GetUserByFirebaseIdResponse(true, user)
    }

    override suspend fun createUser(
        user : FirebaseUser?
    ) : Response<Void>
    {
        return Response.success(null)
    }

    override suspend fun isActivityFavorited(activityId: String?): Response<CheckFavoriteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addFavoriteActivity(activityId: Number): Response<FavoriteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavoriteActivity(activityId: Number): Response<FavoriteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteActivities(): Response<FavoriteActivitiesResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(
        variableMap : Map<String, Any>
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