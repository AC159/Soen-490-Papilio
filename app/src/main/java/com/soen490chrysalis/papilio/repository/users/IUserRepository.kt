package com.soen490chrysalis.papilio.repository.users

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.services.network.requests.SubmitQuiz
import com.soen490chrysalis.papilio.services.network.responses.*
import retrofit2.Response
import java.io.InputStream

/*
    DESCRIPTION:
    Interface that declares all the functionalities that the user repositories (mock or not) will
    have to implement. Having an interface of this kind allows for dependency injection when
    performing unit tests.

    Author: Anastassy Cap
    Date: October 5, 2022
*/
interface IUserRepository
{
    fun initialize(googleSignInClient : GoogleSignInClient)

    fun getUser() : FirebaseUser?

    suspend fun getUserByFirebaseId() : GetUserByFirebaseIdResponse?

    suspend fun createUser(
        user : FirebaseUser?
    ) : Response<Void>

    suspend fun isActivityFavorited(
        activityId : String?
    ) : Triple<Boolean, String, CheckFavoriteResponse>

    suspend fun addFavoriteActivity(
        activityId : Number
    ) : Triple<Boolean, String, FavoriteResponse>

    suspend fun removeFavoriteActivity(
        activityId : Number
    ) : Triple<Boolean, String, FavoriteResponse>

    suspend fun getFavoriteActivities() : Triple<Boolean, String, FavoriteActivitiesResponse>

    suspend fun getJoinedActivities() : Triple<Boolean, String, JoinedActivitiesResponse>

    suspend fun getCreatedActivities() : Triple<Boolean, String, FavoriteActivitiesResponse>

    suspend fun updateUser(
        variableMap : Map<String, Any>
    ) : Triple<Boolean, Int, String>

    suspend fun updateUserProfilePic(
        image : Pair<String, InputStream>
    ) : Pair<Boolean, String>

    suspend fun firebaseAuthWithGoogle(idToken : String) : Pair<Boolean, String>

    suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName : String, lastName : String, emailAddress : String, password : String
    ) : Pair<Boolean, String>

    suspend fun firebaseLoginWithEmailAndPassword(
        emailAddress : String, password : String
    ) : Pair<Boolean, String>

    suspend fun getNewChatTokenForUser(firebaseId : String) : String?

    suspend fun addUserToActivity(activity_id : String) : Pair<Boolean, String>

    suspend fun removeUserFromActivity(activity_id : String) : Pair<Boolean, String>

    suspend fun checkActivityMember(activity_id : String) : CheckActivityMember

    suspend fun submitQuiz(submitQuiz : SubmitQuiz) : Pair<Int, String>

}