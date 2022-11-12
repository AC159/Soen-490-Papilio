package com.soen490chrysalis.papilio.repository

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import okhttp3.ResponseBody
import org.mockito.Mockito
import retrofit2.Response

/*
    DESCRIPTION:
    Mock user repository class for unit tests

    Author: Anastassy Cap
    Date: October 5, 2022
*/
class MockUserRepository : IUserRepository {
    private var firebaseUserMock = Mockito.mock(FirebaseUser::class.java)

    override fun initialize(googleSignInClient : GoogleSignInClient)
    {
    }

    override fun getUser() : FirebaseUser?
    {
        return firebaseUserMock
    }

    override suspend fun createUser(
        user: FirebaseUser?,
        updateDisplayName: Boolean,
        firstName: String?,
        lastName: String?
    ): Response<Void>
    {
        return Response.success(null)
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName: String,
        lastName: String,
        emailAddress: String,
        password: String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }

    override suspend fun firebaseLoginWithEmailAndPassword(
        emailAddress: String,
        password: String
    ) : Pair<Boolean, String>
    {
        return Pair(true, "")
    }


}