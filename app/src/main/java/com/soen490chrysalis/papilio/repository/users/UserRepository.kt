package com.soen490chrysalis.papilio.repository.users

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.soen490chrysalis.papilio.services.network.*
import com.soen490chrysalis.papilio.services.network.requests.User
import com.soen490chrysalis.papilio.services.network.requests.UserRequest
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
class UserRepository(
    private var firebaseAuth : FirebaseAuth,
    private val userService : IUserApiService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IUserRepository
{
    private val logTag = UserRepository::class.java.simpleName

    private var googleSignInClient : GoogleSignInClient? = null

    // This function must be called if we want to start a sign in flow
    override fun initialize(googleSignInClient : GoogleSignInClient)
    {
        this.googleSignInClient = googleSignInClient
    }

    override fun getUser() : FirebaseUser?
    {
        return firebaseAuth.currentUser
    }

    /*
       DESCRIPTION:
       Utility function that makes a request to the backend to create a user.

       The last 3 parameters are there for a good reason: when the user authenticates with their email and password
       instead of google, the firebase object does not have the "displayName" attribute so we need to update the firebase object
       to have a display name because the POST request that we send to the backend requires the firstName and lastName attributes
       so we are simply creating them if they are not existing.

       Author: Anastassy Cap
       Date: November 11, 2022
    */
    override suspend fun createUser( user : FirebaseUser?, updateDisplayName : Boolean, firstName: String?, lastName: String? ) : Response<Void>
    {
        if ( updateDisplayName )
        {
            val userProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName("$firstName $lastName").build()
            user?.updateProfile(userProfileChangeRequest)?.await() // wait for the display name update request to finish before proceeding
        }

        val displayName = user!!.displayName
        val tokens = displayName!!.split(" ")

        val email = user.email
        val firebaseId = user.uid

        val userObjToCreate = User(tokens[0], tokens[1], email!!, firebaseId)
        Log.d(logTag, "simpleUser: $userObjToCreate")

        val response = userService.createUser(UserRequest(userObjToCreate))
        Log.d(logTag, "Create simple user: $response")

        return response
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String) : Pair<Boolean, String>
    {
        return withContext(coroutineDispatcher)
        {
            val response : Pair<Boolean, String> = try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult : AuthResult = firebaseAuth.signInWithCredential(credential).await()

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes : Response<Void> = createUser( authResult.user, false, null, null)
                Log.d(logTag, "userCreationResponse: $userCreationRes")

                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            }
            catch ( e : FirebaseAuthInvalidUserException )
            {
                Log.d(logTag, "FirebaseAuthInvalidUserException: $e")
                Pair(false, "User has been disabled or does not exist!")
            }
            catch ( e : FirebaseAuthInvalidCredentialsException )
            {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException: $e")
                Pair(false, "Invalid credentials!")
            }
            catch ( e : FirebaseAuthUserCollisionException )
            {
                Log.d(logTag, "FirebaseAuthUserCollisionException: $e")
                Pair(false, "Email already exists!")
            }
            catch ( e : Exception )
            {
                Log.d(logTag, "firebaseAuthWithGoogleError: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }

    override suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName : String,
        lastName : String,
        emailAddress: String,
        password: String
    ) : Pair<Boolean, String>
    {
        return withContext(coroutineDispatcher)
        {
            val response : Pair<Boolean, String> = try {
                val authResult : AuthResult = firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).await()

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes : Response<Void> = createUser( authResult.user, true, firstName, lastName )

                Log.d(logTag, "userCreationResponse: $userCreationRes")
                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            }
            catch ( e : FirebaseAuthWeakPasswordException )
            {
                Log.d(logTag, "FirebaseAuthWeakPasswordException $e")
                Pair(false, "Password is too weak!")
            }
            catch ( e : FirebaseAuthInvalidCredentialsException )
            {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException $e")
                Pair(false, "Email address is malformed!")
            }
            catch ( e : FirebaseAuthUserCollisionException )
            {
                Log.d(logTag, "FirebaseAuthUserCollisionException $e")
                Pair(false, "Email already exists!")
            }
            catch ( e : Exception )
            {
                Log.d(logTag, "firebaseCreateAccountWithEmail&Password - createUser() error: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }

    override suspend fun firebaseLoginWithEmailAndPassword(emailAddress: String, password: String) : Pair<Boolean, String>
    {
        return withContext(coroutineDispatcher)
        {
            val response : Pair<Boolean, String> = try {
                val authResult : AuthResult = firebaseAuth.signInWithEmailAndPassword(emailAddress, password).await()

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes : Response<Void> = createUser( authResult.user, false, null, null )
                Log.d(logTag, "userCreationResponse: $userCreationRes")

                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            }
            catch ( e : FirebaseAuthInvalidUserException )
            {
                Log.d(logTag, "FirebaseAuthInvalidUserException: $e")
                Pair(false, "User has been disabled or does not exist!")
            }
            catch ( e : FirebaseAuthInvalidCredentialsException )
            {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException: $e")
                Pair(false, "Wrong password!")
            }
            catch ( e : Exception )
            {
                Log.d(logTag, "firebaseSignInWithEmail&PasswordError: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }
}