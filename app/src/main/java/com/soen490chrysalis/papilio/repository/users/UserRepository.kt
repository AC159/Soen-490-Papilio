package com.soen490chrysalis.papilio.repository.users

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.*
import com.soen490chrysalis.papilio.services.network.*
import com.soen490chrysalis.papilio.services.network.requests.*
import com.soen490chrysalis.papilio.services.network.responses.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

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
    private var firebaseAuth: FirebaseAuth,
    private val userService: IUserApiService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IUserRepository {
    private val logTag = UserRepository::class.java.simpleName

    private var googleSignInClient: GoogleSignInClient? = null

    // This function must be called if we want to start a sign in flow
    override fun initialize(googleSignInClient: GoogleSignInClient) {
        this.googleSignInClient = googleSignInClient
    }

    override fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getUserByFirebaseId(): GetUserByFirebaseIdResponse? {
        return withContext(coroutineDispatcher) {
            try {
                val response = userService.getUserByFirebaseId(firebaseAuth.currentUser?.uid).body()
                Log.d(logTag, "userRepository getUserByFirebaseId() response: $response")
                return@withContext response
            } catch (e: Exception) {
                Log.d(
                    logTag, "userRepository getUserByFirebaseId() exception occurred: ${e.message}"
                )
                println(e.message)
            }
            return@withContext null
        }
    }

    /*
       DESCRIPTION:
       Utility function that calls the backend to obtain a new token for a user to be able to
       use the chat feature.

       Author: Anastassy Cap
       Date: March 4, 2023
     */
    override suspend fun getNewChatTokenForUser(firebaseId: String): String? {
        return withContext(coroutineDispatcher)
        {
            try {
                val response = userService.getUserChatToken(firebaseId)
                Log.d(logTag, "userRepository getNewChatTokenForUser() response: $response")
                return@withContext response.body()
            } catch (e: Exception) {
                Log.d(
                    logTag,
                    "userRepository getNewChatTokenForUser() exception occurred: ${e.message}"
                )
            }
            return@withContext null
        }
    }

    override suspend fun addUserToActivity(
        activity_id: String
    ): Pair<Boolean, String> {
        return withContext(coroutineDispatcher)
        {
            val response: Pair<Boolean, String> = try {
                val requestBody = AddUserToActivityBody(getUser()?.displayName)
                val result = userService.addUserToActivity(getUser()?.uid, activity_id, requestBody)
                Log.d(logTag, "userRepository addUserToActivity() response: $result")
                Pair(result.isSuccessful, result.message())
            } catch (e: Exception) {
                Log.d(logTag, "userRepository addUserToActivity() exception: $e")
                Pair(false, e.message.toString())
            }
            return@withContext response
        }
    }

    override suspend fun removeUserFromActivity(
        activity_id: String
    ): Pair<Boolean, String> {
        return withContext(coroutineDispatcher)
        {
            val response: Pair<Boolean, String> = try {
                val result = userService.removeUserFromActivity(getUser()?.uid, activity_id)
                Log.d(logTag, "userRepository removeUserFromActivity() response: $result")
                Pair(result.isSuccessful, result.message())
            } catch (e: Exception) {
                Log.d(logTag, "userRepository removeUserFromActivity() exception: $e")
                Pair(false, e.message.toString())
            }
            return@withContext response
        }
    }

    override suspend fun checkActivityMember(
        activity_id: String
    ): Triple<Boolean, String, Boolean> {
        return withContext(coroutineDispatcher)
        {
            val response: Triple<Boolean, String, Boolean> = try {
                val result = userService.checkActivityMember(getUser()?.uid, activity_id)
                Log.d(logTag, "userRepository checkActivityMember() response: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!.joined)
            } catch (e: Exception) {
                Log.d(logTag, "userRepository checkActivityMember() exception: $e")
                Triple(false, e.message.toString(), false)
            }
            return@withContext response
        }
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
    override suspend fun createUser(
        user: FirebaseUser?,
    ): Response<Void> {

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

    override suspend fun isActivityFavorited(activityId: String?): Triple<Boolean, String, CheckFavoriteResponse> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val response : Triple<Boolean, String, CheckFavoriteResponse> = try {
                val result = userService.checkActivityFavorited(firebaseId, activityId)
                Log.d(logTag, "isActivityFavorited: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository isActivityFavorited() exception: $e")
                Triple(false, e.message.toString(), CheckFavoriteResponse(false))
            }

            return@withContext response
        }
    }

    override suspend fun addFavoriteActivity(activityId: Number): Triple<Boolean, String, FavoriteResponse> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val editedFields: MutableMap<String, Any> = mutableMapOf()
            editedFields["favoriteActivities"] = activityId

            val response = try {
                val result = userService.addFavoriteActivity(
                    UserUpdate(
                        Identifier(
                            firebaseId = firebaseId
                        ), editedFields
                    )
                )
                Log.d(logTag, "addFavoriteActivity: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository addFavoriteActivity() exception: $e")
                Triple(false, e.message.toString(), FavoriteResponse(false, null))
            }

            return@withContext response
        }
    }

    override suspend fun removeFavoriteActivity(activityId: Number): Triple<Boolean, String, FavoriteResponse>{
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid
            val editedFields: MutableMap<String, Any> = mutableMapOf()
            editedFields["favoriteActivities"] = activityId

            val response = try {
                val result = userService.removeFavoriteActivity(
                    UserUpdate(
                        Identifier(
                            firebaseId = firebaseId
                        ), editedFields
                    )
                )
                Log.d(logTag, "removeFavoriteActivity: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository removeFavoriteActivity() exception: $e")
                Triple(false, e.message.toString(), FavoriteResponse(false, null))
            }

            return@withContext response
        }
    }

    override suspend fun getCreatedActivities(): Triple<Boolean, String, FavoriteActivitiesResponse> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val response = try {
                val result = userService.getUserActivities(firebaseId)
                Log.d(logTag, "getCreatedActivities: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository getCreatedActivities() exception: $e")
                Triple(false, e.message.toString(), FavoriteActivitiesResponse("0", listOf<ActivityObject>()))
            }

            return@withContext response
        }
    }

    override suspend fun getFavoriteActivities(): Triple<Boolean, String, FavoriteActivitiesResponse> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val response = try {
                val result = userService.getUserFavoriteActivities(firebaseId)
                Log.d(logTag, "getFavoriteActivities: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository getFavoriteActivities() exception: $e")
                Triple(false, e.message.toString(), FavoriteActivitiesResponse("0", listOf<ActivityObject>()))
            }

            return@withContext response
        }
    }

    override suspend fun getJoinedActivities(): Triple<Boolean, String, JoinedActivitiesResponse> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val response = try {
                val result = userService.getUserJoinedActivities(firebaseId)
                Log.d(logTag, "getJoinedActivities: $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e: Exception) {
                Log.d(logTag, "userRepository getJoinedActivities() exception: $e")
                Triple(false, e.message.toString(), JoinedActivitiesResponse("0", listOf<JoinedActivityObject>()))
            }

            return@withContext response
        }
    }

    /*
       DESCRIPTION:
       Utility function that makes a request to the backend to update a user.

       The key (and only) argument for this function is "variableMap", which is a MutableMap object that contains a map of all the
       edited fields in the user profile page. In short, all fields that the user changed in the user profile get updated in one single PUT request
       and all the edited fields are kept in the aforementioned MutableMap, which gets sent along with the PUT request as the Body of the request.

       Author: Anas Peerzada
       Date: January 1st, 2023
    */
    override suspend fun updateUser(
        variableMap: Map<String, Any>
    ): Response<Void> {
        return withContext(coroutineDispatcher) {
            val firebaseId = firebaseAuth.currentUser!!.uid

            val response = userService.updateUser(UserUpdate(Identifier(firebaseId), variableMap))

            Log.d(logTag, "Update user: $response")
            return@withContext response
        }
    }

    override suspend fun updateUserProfilePic(
        image: Pair<String, InputStream>
    ): Response<Void> {
        return withContext(coroutineDispatcher) {
                val firebaseId = firebaseAuth.currentUser!!.uid

                val inputStream = image.second
                val imageFileExtension = image.first

                val file = File.createTempFile("tempFile", null, null)
                val out: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                inputStream.close()

                val currentImage = MultipartBody.Part.createFormData(
                    "image", // this name must match the name given in the backend
                    file.name,
                    file.asRequestBody("image/$imageFileExtension".toMediaType())
                )

                val response = userService.updateUserProfilePic(firebaseId, currentImage)

                Log.d(logTag, "Update user profile pic: $response")
                return@withContext response
        }
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String): Pair<Boolean, String> {
        return withContext(coroutineDispatcher) {
            val response: Pair<Boolean, String> = try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult: AuthResult = firebaseAuth.signInWithCredential(credential).await()

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes: Response<Void> = createUser(authResult.user)
                Log.d(logTag, "userCreationResponse: $userCreationRes")

                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.d(logTag, "FirebaseAuthInvalidUserException: $e")
                Pair(false, "User has been disabled or does not exist!")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException: $e")
                Pair(false, "Invalid credentials!")
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.d(logTag, "FirebaseAuthUserCollisionException: $e")
                Pair(false, "Email already exists!")
            } catch (e: Exception) {
                Log.d(logTag, "firebaseAuthWithGoogleError: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }

    override suspend fun firebaseCreateAccountWithEmailAndPassword(
        firstName: String, lastName: String, emailAddress: String, password: String
    ): Pair<Boolean, String> {
        return withContext(coroutineDispatcher) {
            val response: Pair<Boolean, String> = try {
                val authResult: AuthResult =
                    firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).await()

                // Update the user's display name
                val userProfileChangeRequest =
                    UserProfileChangeRequest.Builder().setDisplayName("$firstName $lastName")
                        .build()
                authResult.user?.updateProfile(userProfileChangeRequest)
                    ?.await() // wait for the display name update request to finish before proceeding

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes: Response<Void> = createUser(firebaseAuth.currentUser)

                Log.d(logTag, "userCreationResponse: $userCreationRes")
                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            } catch (e: FirebaseAuthWeakPasswordException) {
                Log.d(logTag, "FirebaseAuthWeakPasswordException $e")
                Pair(false, "Password is too weak!")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException $e")
                Pair(false, "Email address is malformed!")
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.d(logTag, "FirebaseAuthUserCollisionException $e")
                Pair(false, "Email already exists!")
            } catch (e: Exception) {
                Log.d(logTag, "firebaseCreateAccountWithEmail&Password - createUser() error: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }

    override suspend fun firebaseLoginWithEmailAndPassword(
        emailAddress: String, password: String
    ): Pair<Boolean, String> {
        return withContext(coroutineDispatcher) {
            val response: Pair<Boolean, String> = try {
                val authResult: AuthResult =
                    firebaseAuth.signInWithEmailAndPassword(emailAddress, password).await()

                // Now that we have successfully authenticated, we can create a user in the database
                val userCreationRes: Response<Void> = createUser(authResult.user)
                Log.d(logTag, "userCreationResponse: $userCreationRes")

                Pair(userCreationRes.isSuccessful, userCreationRes.message())
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.d(logTag, "FirebaseAuthInvalidUserException: $e")
                Pair(false, "User has been disabled or does not exist!")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.d(logTag, "FirebaseAuthInvalidCredentialsException: $e")
                Pair(false, "Wrong password!")
            } catch (e: Exception) {
                Log.d(logTag, "firebaseSignInWithEmail&PasswordError: $e")
                Pair(false, e.message.toString())
            }

            return@withContext response
        }
    }
}