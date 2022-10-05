package com.soen490chrysalis.papilio

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import org.mockito.Mockito

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

    override fun firebaseAuthWithGoogle(idToken: String, authResultCallBack : (authResult : Boolean) -> Unit)
    {
        authResultCallBack(true) // authentication is successful
    }

    fun failFirebaseAuthWithGoogle(idToken: String, authResultCallBack : (authResult : Boolean) -> Unit)
    {
        authResultCallBack(false) // authentication is successful
    }
}