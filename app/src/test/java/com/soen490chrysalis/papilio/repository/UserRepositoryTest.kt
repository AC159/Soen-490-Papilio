package com.soen490chrysalis.papilio.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.*
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.services.network.responses.*
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


@RunWith(JUnit4::class)
class UserRepositoryTest
{
    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IUserApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockAuthTask = Mockito.mock(Task::class.java)
    private val mockAuthResult = Mockito.mock(AuthResult::class.java)
    private val mockGoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)

    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirebaseUserUid = "aset23q45346457sdfhrtu5r"

    private lateinit var userRepository : IUserRepository
    private val firstName = "firstName"
    private val lastName = "lastName"
    private val email = "someEmail@gmail.com"
    private val password = "password"

    private val activity_id = "160"

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp()
    {
        // Stub firebase authentication functions
        @Suppress("UNCHECKED_CAST")
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(
            mockAuthTask as Task<AuthResult>?
        )

        /*  The next 3 lines are very important when it comes to calling functions that return tasks
            on which we are calling the '.await()' function. In order to not make the '.await()' hang,
            we must return a TaskCompletionSource<AuthResult> that will return a task which will not make
            the '.await()' function block anymore. */
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setResult(mockAuthResult)
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        Mockito.`when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser.displayName).thenReturn("$firstName $lastName")
        Mockito.`when`(mockFirebaseUser.email).thenReturn(email)
        Mockito.`when`(mockFirebaseUser.uid).thenReturn(mockFirebaseUserUid)

        mockWebServer.start()
        println("Webserver has successfully started for UserRepository test...")

        mockRetrofitUserService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IUserApiService::class.java)

        println("Instantiated mockRetrofitUserService for UserRepository test!")

        // Important to initialize the user repository here since the mockRetrofitUserService needs to be create beforehand
        userRepository = Mockito.spy(UserRepository(mockFirebaseAuth, mockRetrofitUserService))
        userRepository.initialize(mockGoogleSignInClient) // this line is just to get more test coverage

        // Mock all log calls
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        println("Setup method is done!")
    }

    @After
    fun teardown()
    {
        mockWebServer.shutdown()
    }

    @Test
    fun getFirebaseUser()
    {
        userRepository.getUser()
        verify(mockFirebaseAuth, times(1)).currentUser
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getUserByFirebaseId() = runTest {
        // Test the route with a firebase id of null
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser?.uid).thenReturn(null)

        var response = userRepository.getUserByFirebaseId()
        assert(response == null)

        // Let's test the route with a valid firebase id
        val user = UserObject(
            "first",
            "last",
            "validEmail@gmail.com",
            "hQH3m5B4dUXoHXvbneslxcuCHR52",
            "1",
            null,
            "2022-11-14T02:07:02.585Z",
            "2022-11-14T02:07:02.585Z",
            "Hello! It's me, firstName!",
            "ewfj13498to3ifj0193rfg93rtg"
        )

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"found\": true,\n" +
                    "    \"user\": {\n" +
                    "        \"firebase_id\": \"hQH3m5B4dUXoHXvbneslxcuCHR52\",\n" +
                    "        \"firstName\": \"first\",\n" +
                    "        \"lastName\": \"last\",\n" +
                    "        \"countryCode\": \"1\",\n" +
                    "        \"phone\": null,\n" +
                    "        \"email\": \"validEmail@gmail.com\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"bio\": \"Hello! It's me, firstName!\",\n" +
                    "        \"image\": \"ewfj13498to3ifj0193rfg93rtg\"\n" +
                    "    }\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser?.uid).thenReturn(mockFirebaseUserUid)

        response = userRepository.getUserByFirebaseId()
        println("Response: $response")

        assert(response!!.user == user)
        Mockito.verify(mockFirebaseAuth, times(2)).currentUser
        Mockito.verify(mockFirebaseUser, times(2))?.uid
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firebaseAuthWithGoogleTest() = runTest {
        /* It is important to enqueue mock responses to the web server if we are going to make API calls in the test, otherwise
            the result will not be successful. */
        val mockedResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockedResponse)

        val idToken = "someToken"
        val result : Pair<Boolean, String> = userRepository.firebaseAuthWithGoogle(idToken)
        println("Result: $result")
        assert(!result.first && result.second == "firebaseAuth.signInWithCredential(credential) must not be null")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPasswordTest() = runTest {
        /* It is important to enqueue mock responses to the web server if we are going to make API calls in the test, otherwise
            the result will not be successful. */
        val mockedResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockedResponse)

        val result : Pair<Boolean, String> =
            userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName,
                lastName,
                email,
                password
            )

        println("Result: $result")

        assert(result.first && result.second == "OK")
        Mockito.verify(mockFirebaseAuth, times(1)).createUserWithEmailAndPassword(email, password)
        Mockito.verify(userRepository, times(1)).createUser(mockFirebaseUser)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPasswordWithFirebaseAuthWeakPasswordException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(
            FirebaseAuthWeakPasswordException(
                "400",
                "Weak password!",
                null
            )
        )
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName,
                lastName,
                email,
                password
            )
        println("Result: $result")

        assert(!result.first && result.second == "Password is too weak!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPasswordWithFirebaseAuthInvalidCredentialsException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(
            FirebaseAuthInvalidCredentialsException(
                "400",
                "Malformed email address!"
            )
        )
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName,
                lastName,
                email,
                password
            )
        println("Result: $result")

        assert(!result.first && result.second == "Email address is malformed!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPasswordWithFirebaseAuthUserCollisionException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(
            FirebaseAuthUserCollisionException(
                "400",
                "Email already exists!"
            )
        )
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName,
                lastName,
                email,
                password
            )
        println("Result: $result")

        assert(!result.first && result.second == "Email already exists!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPasswordWithGenericException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(Exception("Oops, something went wrong!"))
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseCreateAccountWithEmailAndPassword(
                firstName,
                lastName,
                email,
                password
            )
        println("Result: $result")

        assert(!result.first && result.second == "Oops, something went wrong!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun signInWithEmailAndPassword() = runTest {
        /* It is important to enqueue mock responses to the web server if we are going to make API calls in the test, otherwise
            the result will not be successful. */
        val mockedResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockedResponse)

        val result : Pair<Boolean, String> =
            userRepository.firebaseLoginWithEmailAndPassword(email, password)
        println("Result: $result")

        assert(result.first && result.second == "OK")
        Mockito.verify(mockFirebaseAuth, times(1)).signInWithEmailAndPassword(email, password)
        Mockito.verify(userRepository, times(1)).createUser(mockFirebaseUser)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun signInWithEmailAndPasswordWithFirebaseAuthInvalidUserException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(FirebaseAuthInvalidUserException("400", "Invalid User!"))
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseLoginWithEmailAndPassword(email, password)
        println("Result: $result")
        assert(!result.first && result.second == "User has been disabled or does not exist!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun signInWithEmailAndPasswordWithFirebaseAuthInvalidCredentialsException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(
            FirebaseAuthInvalidCredentialsException(
                "400",
                "Invalid Credentials!"
            )
        )
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseLoginWithEmailAndPassword(email, password)
        println("Result: $result")
        assert(!result.first && result.second == "Wrong password!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun signInWithEmailAndPasswordWithGenericException() = runTest {
        val taskCompletionSource = TaskCompletionSource<AuthResult>()
        taskCompletionSource.setException(Exception("Oops, something went wrong!"))
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.task)

        val result : Pair<Boolean, String> =
            userRepository.firebaseLoginWithEmailAndPassword(email, password)
        println("Result: $result")
        assert(!result.first && result.second == "Oops, something went wrong!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getChatToken() = runTest {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSnNNVlhnVDJNaE44eGpwVzFOTnZBTXFMQURmMSJ9.N-FhnRWLgkGP6knf_QD7gWgUJ7Fm4wtbKkodAUqSlwU"
        val mockServerResponse = MockResponse().setResponseCode(200).setBody(token)
        mockWebServer.enqueue(mockServerResponse)

        var userToken = userRepository.getNewChatTokenForUser(mockFirebaseUserUid)
        println("user token: $userToken")
        assert(userToken == token)

        // Ask for a token again but this time the server will return null
        userToken = userRepository.getNewChatTokenForUser(mockFirebaseUserUid)
        assert(userToken == null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkActivityMembershipTest() = runTest {
        var mockServerResponse = MockResponse().setResponseCode(200).setBody("{\"joined\": true}")
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.checkActivityMember(activity_id)
        println("Result: $result")
        assert(result.first && result.second == "OK" && result.third)

        mockServerResponse = MockResponse().setResponseCode(200).setBody("{\"joined\": false}")
        mockWebServer.enqueue(mockServerResponse)

        result = userRepository.checkActivityMember(activity_id)
        println("Result: $result")
        assert(result.first && result.second == "OK" && !result.third)

        mockServerResponse = MockResponse().setResponseCode(400)
        mockWebServer.enqueue(mockServerResponse)

        result = userRepository.checkActivityMember(activity_id)
        println("Result: $result")
        @Suppress("SENSELESS_COMPARISON")
        assert(!result.first && result.second == "null" && !result.third)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addUserToActivityChatTest() = runTest {
        var mockServerResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.addUserToActivity(activity_id)
        println("Result: $result")
        assert(result.first && result.second == "OK")

        mockServerResponse = MockResponse().setResponseCode(400)
        mockWebServer.enqueue(mockServerResponse)

        result = userRepository.addUserToActivity(activity_id)
        println("Result: $result")
        assert(!result.first && result.second == "Client Error")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeUserFromActivityTest() = runTest {
        var mockServerResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.removeUserFromActivity(activity_id)
        println("Result: $result")
        assert(result.first && result.second == "OK")

        mockServerResponse = MockResponse().setResponseCode(400)
        mockWebServer.enqueue(mockServerResponse)

        result = userRepository.removeUserFromActivity(activity_id)
        println("Result: $result")
        assert(!result.first && result.second == "Client Error")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isActivityFavorited() = runTest {

        var mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("{\"isActivityFound\": true}")
        mockWebServer.enqueue(mockedResponse)

        var result = userRepository.isActivityFavorited(activity_id)
        advanceUntilIdle()
        println("Result: $result")
        assert(result.first && result.second == "OK" && result.third.isActivityFound)

        mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\"isActivityFound\": false}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.isActivityFavorited(activity_id)
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK" && !result.third.isActivityFound)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addFavoriteActivity() = runTest {
        val favoriteActivities = IntArray(5)
        favoriteActivities[0] = 1
        favoriteActivities[1] = 2
        favoriteActivities[2] = 3
        favoriteActivities[3] = 4
        favoriteActivities[4] = 5

        val update = FavoriteUserObject(
            "hQH3m5B4dUXoHXvbneslxcuCHR52",
            "first",
            "last",
            "1",
            "null",
            "validEmail@gmail.com",
            "Hello! It's me, firstName!",
            favoriteActivities
        )

        val responseObject = FavoriteResponse(true, update)

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"success\": true,\n" +
                    "    \"update\": {\n" +
                    "        \"firebase_id\": \"hQH3m5B4dUXoHXvbneslxcuCHR52\",\n" +
                    "        \"firstName\": \"first\",\n" +
                    "        \"lastName\": \"last\",\n" +
                    "        \"countryCode\": \"1\",\n" +
                    "        \"phone\": null,\n" +
                    "        \"email\": \"validEmail@gmail.com\",\n" +
                    "        \"favoriteActivities\": [1,2,3,4,5],\n" +
                    "        \"bio\": \"Hello! It's me, firstName!\"\n" +
                    "    }\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.addFavoriteActivity(Integer.parseInt(activity_id))
        println("Result: $result")
        assert(result.third.toString() == responseObject.toString())

        val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"success\": false,\n" +
                        "    \"update\": null \n" +
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.addFavoriteActivity(Integer.parseInt(activity_id))
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeFavoriteActivity() = runTest {
        val favoriteActivities = IntArray(5)
        favoriteActivities[0] = 1
        favoriteActivities[1] = 2
        favoriteActivities[2] = 3
        favoriteActivities[3] = 4
        favoriteActivities[4] = 5

        val update = FavoriteUserObject(
            "hQH3m5B4dUXoHXvbneslxcuCHR52",
            "first",
            "last",
            "1",
            "null",
            "validEmail@gmail.com",
            "Hello! It's me, firstName!",
            favoriteActivities
        )

        val responseObject = FavoriteResponse(true, update)

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"success\": true,\n" +
                    "    \"update\": {\n" +
                    "        \"firebase_id\": \"hQH3m5B4dUXoHXvbneslxcuCHR52\",\n" +
                    "        \"firstName\": \"first\",\n" +
                    "        \"lastName\": \"last\",\n" +
                    "        \"countryCode\": \"1\",\n" +
                    "        \"phone\": null,\n" +
                    "        \"email\": \"validEmail@gmail.com\",\n" +
                    "        \"favoriteActivities\": [1,2,3,4,5],\n" +
                    "        \"bio\": \"Hello! It's me, firstName!\"\n" +
                    "    }\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.removeFavoriteActivity(Integer.parseInt(activity_id))
        println("Result: $result")
        assert(result.third.toString() == responseObject.toString())

        val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"success\": false,\n" +
                        "    \"update\": null \n" +
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.removeFavoriteActivity(69)
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCreatedActivities() = runTest {
        val activities : MutableList<ActivityObject> = mutableListOf()
        val images : MutableList<String> = mutableListOf()
        images.add("http://first-image-url.jpg")
        images.add("http://second-image-url.jpg")
        images.add("http://third-image-url.jpg")

        activities.add(
            ActivityObject(
                "1234",
                "Karting activity",
                "Go race with your friends!",
                "0",
                "1000",
                "15",
                images,
                "19h00",
                "21h00",
                "200 Nowhere street, Quebec, Canada",
                "2022-11-14T02:07:02.585Z",
                "2022-11-14T02:07:02.585Z",
                null,
                "userId123"
            )
        )

        activities.add(
            ActivityObject(
                "5678",
                "Soccer game",
                "Play with your friends!",
                "0",
                "0",
                "22",
                images,
                "19h00",
                "21h00",
                "200 Nowhere street, Quebec, Canada",
                "2022-11-14T02:07:02.585Z",
                "2022-11-14T02:07:02.585Z",
                null,
                "userId123"
            )
        )

        val expectedResponse = FavoriteActivitiesResponse(activities.size.toString(), activities)

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"count\": 2,\n" +
                    "    \"activities\": [" +
                    "       {\n" +
                    "        \"id\": \"1234\",\n" +
                    "        \"title\": \"Karting activity\",\n" +
                    "        \"description\": \"Go race with your friends!\",\n" +
                    "        \"costPerIndividual\": \"0\",\n" +
                    "        \"costPerGroup\": 1000,\n" +
                    "        \"groupSize\": \"15\",\n" +
                    "        \"images\": [\"http://first-image-url.jpg\", \"http://second-image-url.jpg\", \"http://third-image-url.jpg\"],\n" +
                    "        \"startTime\": \"19h00\",\n" +
                    "        \"endTime\": \"21h00\",\n" +
                    "        \"address\": \"200 Nowhere street, Quebec, Canada\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"business\": null,\n" +
                    "        \"userId\": \"userId123\"\n" +
                    "       },\n" +
                    "       {\n" +
                    "        \"id\": \"5678\",\n" +
                    "        \"title\": \"Soccer game\",\n" +
                    "        \"description\": \"Play with your friends!\",\n" +
                    "        \"costPerIndividual\": \"0\",\n" +
                    "        \"costPerGroup\": 0,\n" +
                    "        \"groupSize\": \"22\",\n" +
                    "        \"images\": [\"http://first-image-url.jpg\", \"http://second-image-url.jpg\", \"http://third-image-url.jpg\"],\n" +
                    "        \"startTime\": \"19h00\",\n" +
                    "        \"endTime\": \"21h00\",\n" +
                    "        \"address\": \"200 Nowhere street, Quebec, Canada\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"business\": null,\n" +
                    "        \"userId\": \"userId123\"\n" +
                    "       }" +
                    "   ]\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.getCreatedActivities()
        println("Result: $result")
        assert(result.third.toString() == expectedResponse.toString())

        val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"count\": \"0\",\n" +
                        "    \"activities\": null, \n"+
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.getCreatedActivities()
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFavoriteActivities() = runTest {
        val activities : MutableList<ActivityObject> = mutableListOf()
        val images : MutableList<String> = mutableListOf()
        images.add("http://first-image-url.jpg")
        images.add("http://second-image-url.jpg")
        images.add("http://third-image-url.jpg")

        activities.add(
            ActivityObject(
                "1234",
                "Karting activity",
                "Go race with your friends!",
                "0",
                "1000",
                "15",
                images,
                "19h00",
                "21h00",
                "200 Nowhere street, Quebec, Canada",
                "2022-11-14T02:07:02.585Z",
                "2022-11-14T02:07:02.585Z",
                null,
                "userId123"
            )
        )

        activities.add(
            ActivityObject(
                "5678",
                "Soccer game",
                "Play with your friends!",
                "0",
                "0",
                "22",
                images,
                "19h00",
                "21h00",
                "200 Nowhere street, Quebec, Canada",
                "2022-11-14T02:07:02.585Z",
                "2022-11-14T02:07:02.585Z",
                null,
                "userId123"
            )
        )

        val expectedResponse = FavoriteActivitiesResponse(activities.size.toString(), activities)

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"count\": 2,\n" +
                    "    \"activities\": [" +
                    "       {\n" +
                    "        \"id\": \"1234\",\n" +
                    "        \"title\": \"Karting activity\",\n" +
                    "        \"description\": \"Go race with your friends!\",\n" +
                    "        \"costPerIndividual\": \"0\",\n" +
                    "        \"costPerGroup\": 1000,\n" +
                    "        \"groupSize\": \"15\",\n" +
                    "        \"images\": [\"http://first-image-url.jpg\", \"http://second-image-url.jpg\", \"http://third-image-url.jpg\"],\n" +
                    "        \"startTime\": \"19h00\",\n" +
                    "        \"endTime\": \"21h00\",\n" +
                    "        \"address\": \"200 Nowhere street, Quebec, Canada\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"business\": null,\n" +
                    "        \"userId\": \"userId123\"\n" +
                    "       },\n" +
                    "       {\n" +
                    "        \"id\": \"5678\",\n" +
                    "        \"title\": \"Soccer game\",\n" +
                    "        \"description\": \"Play with your friends!\",\n" +
                    "        \"costPerIndividual\": \"0\",\n" +
                    "        \"costPerGroup\": 0,\n" +
                    "        \"groupSize\": \"22\",\n" +
                    "        \"images\": [\"http://first-image-url.jpg\", \"http://second-image-url.jpg\", \"http://third-image-url.jpg\"],\n" +
                    "        \"startTime\": \"19h00\",\n" +
                    "        \"endTime\": \"21h00\",\n" +
                    "        \"address\": \"200 Nowhere street, Quebec, Canada\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"business\": null,\n" +
                    "        \"userId\": \"userId123\"\n" +
                    "       }" +
                    "   ]\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.getFavoriteActivities()
        println("Result: $result")
        assert(result.third.toString() == expectedResponse.toString())

        val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"count\": \"0\",\n" +
                        "    \"activities\": null, \n"+
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.getFavoriteActivities()
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getJoinedActivities() = runTest {
        val activities : MutableList<JoinedActivityObject> = mutableListOf()
        val images : MutableList<String> = mutableListOf()
        images.add("http://first-image-url.jpg")
        images.add("http://second-image-url.jpg")
        images.add("http://third-image-url.jpg")

        activities.add(
            JoinedActivityObject(
                "1234",
                "userId1234",
                "activityId_1234",
                ActivityObject(
                    "1234",
                    "Karting activity",
                    "Go race with your friends!",
                    "0",
                    "1000",
                    "15",
                    images,
                    "19h00",
                    "21h00",
                    "200 Nowhere street, Quebec, Canada",
                    "2022-11-14T02:07:02.585Z",
                    "2022-11-14T02:07:02.585Z",
                    null,
                    "userId123"
                )
            )
        )

        val expectedResponse = JoinedActivitiesResponse(activities.size.toString(), activities)

        val mockServerResponse = MockResponse().setResponseCode(200).setBody(
            "{\n" +
                    "    \"count\": 1,\n" +
                    "    \"row\": [" +
                    "       {\n" +
                    "        \"id\": \"1234\",\n" +
                    "        \"userId\": \"userId1234\",\n" +
                    "        \"activityId\": \"activityId_1234\",\n" +
                    "        \"activity\": {\n" +
                    "               \"id\": \"1234\",\n" +
                    "               \"title\": \"Karting activity\",\n" +
                    "               \"description\": \"Go race with your friends!\",\n" +
                    "               \"costPerIndividual\": \"0\",\n" +
                    "               \"costPerGroup\": 1000,\n" +
                    "               \"groupSize\": \"15\",\n" +
                    "               \"images\": [\"http://first-image-url.jpg\", \"http://second-image-url.jpg\", \"http://third-image-url.jpg\"],\n" +
                    "               \"startTime\": \"19h00\",\n" +
                    "               \"endTime\": \"21h00\",\n" +
                    "               \"address\": \"200 Nowhere street, Quebec, Canada\",\n" +
                    "               \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "               \"updatedAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "               \"business\": null,\n" +
                    "               \"userId\": \"userId123\"\n" +
                    "           }\n" +
                    "       }\n" +
                    "   ]\n" +
                    "}"
        )
        mockWebServer.enqueue(mockServerResponse)

        var result = userRepository.getJoinedActivities()
        println("Result: $result")
        assert(result.third.toString() == expectedResponse.toString())

        val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"count\": \"0\",\n" +
                        "    \"row\": null, \n"+
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.getJoinedActivities()
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first && result.second != "OK")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateUserProfilePic() = runTest {

        var mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("{\n" +
                        "    \"success\": true,\n" +
                        "    \"update\": {\n" +
                        "        \"firebaseId\": \"er23523rt23t23t\",\n" +
                        "        \"firstName\": \"firstName\",\n" +
                        "        \"lastName\": \"lastName\",\n" +
                        "        \"countryCode\": 1,\n" +
                        "        \"phone\": \"4353622626\",\n" +
                        "        \"email\": \"someemail@gmail.com\",\n" +
                        "        \"bio\": \"Yo\",\n" +
                        "        \"favoriteActivities\": null,\n" +
                        "        \"image\": \"sff23532gf24g4ry45y\"\n" +
                        "  }\n" +
                        "}")
        mockWebServer.enqueue(mockedResponse)

        val file = File.createTempFile("temp-file", "_temp")
        var result = userRepository.updateUserProfilePic(Pair("jpg", file.inputStream()))
        advanceUntilIdle()
        println("Result: $result")
        assert(result.first)

        mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody("{\n" +
                        "    \"success\": false,\n" +
                        "    \"update\": null \n"  +
                        "}")
        mockWebServer.enqueue(mockedResponse)

        result = userRepository.updateUserProfilePic(Pair("jpg", file.inputStream()))
        advanceUntilIdle()
        println("Result: $result")
        assert(!result.first)
    }
}