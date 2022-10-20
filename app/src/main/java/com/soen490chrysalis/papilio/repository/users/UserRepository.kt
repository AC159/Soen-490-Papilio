package com.soen490chrysalis.papilio.repository.users

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/*
    DESCRIPTION:
    UserRepository class that handles the logic to authenticate the user with firebase.
    It implements the IUserRepository interface primarily to improve testability of the corresponding
    view models that use this repository.

    Potential further changes needed:
    - Each repository will probably require a Data Access Object (DAO) to be passed as a parameter
    to its primary constructor. This DAO will perform all the API/database calls needed to retrieve
    user related information

    Author: Anastassy Cap
    Date: October 5, 2022
*/
class UserRepository : IUserRepository
{
    private var firebaseAuth : FirebaseAuth? = null
    private var googleSignInClient : GoogleSignInClient? = null

    // This function must be called if we want to start a sign in flow
    override fun initialize(googleSignInClient : GoogleSignInClient)
    {
        this.googleSignInClient = googleSignInClient

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun getUser() : FirebaseUser?
    {
        if ( firebaseAuth == null )
        {
            // Initialize Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance()
        }
        return firebaseAuth!!.currentUser
    }

    private fun authTaskCompletedCallback( authTask: Task<AuthResult>, viewCallBack : (authResult : Boolean, errorMessage: String) -> Unit )
    {
        Log.d(Log.DEBUG.toString(), "Auth task has finished: $authTask")
        if (authTask.isSuccessful)
        {
            // Sign in success, update UI with the signed-in user's information
            Log.d(Log.DEBUG.toString(), "signInWithCredential:success")
            val user = getUser()
            if (user != null) {
                Log.d(Log.DEBUG.toString(), "Current user: ${user.email}")
                /* Calling the callback function passed as a parameter will trigger the
                observer in the LoginActivity which will redirect the user to a new page */
                viewCallBack(true, "")
            }
        }
        else
        {
            // If sign in fails, display a message to the user.
            Log.w(Log.DEBUG.toString(), "signInWithCredential:failure", authTask.exception)
            viewCallBack(false, authTask.exception?.message.toString())
        }
    }

    override fun firebaseAuthWithGoogle(idToken: String, authResultCallBack : (authResult : Boolean, errorMessage: String) -> Unit)
    {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authTask : Task<AuthResult> = firebaseAuth!!.signInWithCredential(credential)

        authTask.addOnCompleteListener { task ->
            authTaskCompletedCallback(task, authResultCallBack)
        }
    }

    override fun firebaseCreateAccountWithEmailAndPassword(
        emailAddress: String,
        password: String,
        authResultCallBack: (authResult: Boolean, errorMessage: String) -> Unit
    )
    {
        val authTask : Task<AuthResult> = firebaseAuth!!.createUserWithEmailAndPassword(emailAddress, password)
        authTask.addOnCompleteListener { task ->
            authTaskCompletedCallback(task, authResultCallBack)
        }
    }

    override fun firebaseLoginWithEmailAndPassword(
        emailAddress: String,
        password: String,
        authResultCallBack: (authResult: Boolean, errorMessage: String) -> Unit
    ) {
        val authTask : Task<AuthResult> = firebaseAuth!!.signInWithEmailAndPassword(emailAddress, password)
        authTask.addOnCompleteListener { task ->
            authTaskCompletedCallback(task, authResultCallBack)
        }
    }
}