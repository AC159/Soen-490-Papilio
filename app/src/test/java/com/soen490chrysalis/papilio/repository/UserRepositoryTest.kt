package com.soen490chrysalis.papilio.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.*
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.services.network.responses.UserObject
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException


@RunWith(JUnit4::class)
class UserRepositoryTest {
    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService: IUserApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockAuthTask = Mockito.mock(Task::class.java)
    private val mockAuthResult = Mockito.mock(AuthResult::class.java)
    private val mockGoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)

    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirebaseUserUid = "aset23q45346457sdfhrtu5r"

    private lateinit var userRepository: IUserRepository
    private val firstName = "firstName"
    private val lastName = "lastName"
    private val email = "someEmail@gmail.com"
    private val password = "password"

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
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
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getFirebaseUser() {
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
        val result: Pair<Boolean, String> = userRepository.firebaseAuthWithGoogle(idToken)
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
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

        val result: Pair<Boolean, String> =
            userRepository.firebaseLoginWithEmailAndPassword(email, password)
        println("Result: $result")
        assert(!result.first && result.second == "Oops, something went wrong!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getChatToken() = runTest {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSnNNVlhnVDJNaE44eGpwVzFOTnZBTXFMQURmMSJ9.N-FhnRWLgkGP6knf_QD7gWgUJ7Fm4wtbKkodAUqSlwU"
        val mockServerResponse = MockResponse().setResponseCode(200).setBody(token)
        mockWebServer.enqueue(mockServerResponse)

        var userToken = userRepository.getNewChatTokenForUser(mockFirebaseUserUid)
        println("user token: $userToken")
        assert(userToken == token)

        // Ask for a token again but this time the server will return null
        userToken = userRepository.getNewChatTokenForUser(mockFirebaseUserUid)
        assert(userToken == null)
    }

    @Suppress("LocalVariableName")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addUserToActivityChatTest() = runTest {
        val activity_id = "160"

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
}