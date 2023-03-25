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
        val activityObject1 = JoinedActivityObject(
            "1", "wer232f23f", "thy2563tyj", ActivityObject(
                "1",
                "Activity Title",
                "This is Activity 1",
                "0",
                "0",
                "4",
                listOf("a" + 1 + "image1", "a" + 2 + "image1"),
                "2023-3-22T",
                "2023-3-22T",
                "Activity 1 Address",
                "A1 Creation Time",
                "A1 Update Time",
                null,
                "user 1"
            )
        )

        val activityObject2 = JoinedActivityObject(
            "2", "wer23w2f23f", "thy25163tyj", ActivityObject(
                "2",
                "Activity Title",
                "This is Activity 2",
                "0",
                "0",
                "4",
                listOf("a" + 1 + "image1", "a" + 2 + "image1"),
                "2023-3-25T",
                "2023-3-25T",
                "Activity 1 Address",
                "A1 Creation Time",
                "A1 Update Time",
                null,
                "user 1"
            )
        )

        val activityList = listOf(activityObject1, activityObject2)

        return Triple(
            true, "", JoinedActivitiesResponse(activityList.count().toString(), activityList)
        )
    }

    override suspend fun getCreatedActivities() : Triple<Boolean, String, FavoriteActivitiesResponse>
    {
        val activityObject3 = ActivityObject(
            "3",
            "Activity Title",
            "This is Activity 3",
            "0",
            "0",
            "4",
            listOf("a" + 1 + "image1", "a" + 2 + "image1"),
            "2023-2-25T",
            "2023-2-25T",
            "Activity 1 Address",
            "A1 Creation Time",
            "A1 Update Time",
            null,
            "user 1"
        )

        val activityObject4 = ActivityObject(
            "4",
            "Activity Title",
            "This is Activity 4",
            "0",
            "0",
            "4",
            listOf("a" + 1 + "image1", "a" + 2 + "image1"),
            "2024-3-23T",
            "2024-3-23T",
            "Activity 1 Address",
            "A1 Creation Time",
            "A1 Update Time",
            null,
            "user 1"
        )

        val activityList = listOf(activityObject3, activityObject4)

        return Triple(
            true, "", FavoriteActivitiesResponse(activityList.count().toString(), activityList)
        )
    }

    override suspend fun updateUser(
        variableMap : Map<String, Any>
    ) : Triple<Boolean, Int, String>
    {
        return Triple(true, 200, "OK")
    }

    override suspend fun updateUserProfilePic(
        image : Pair<String, InputStream>
    ) : Pair<Boolean, String>
    {
        return Pair(true, "OK")
    }

    override suspend fun firebaseAuthWithGoogle(idToken : String) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName : String, lastName : String, emailAddress : String, password : String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseLoginWithEmailAndPassword(
        emailAddress : String, password : String
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