package com.soen490chrysalis.papilio.repository.users

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser

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

    fun firebaseAuthWithGoogle(idToken: String, authResultCallBack : (authResult : Boolean) -> Unit)
}