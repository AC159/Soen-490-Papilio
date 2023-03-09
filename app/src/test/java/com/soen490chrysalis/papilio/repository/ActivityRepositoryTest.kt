package com.soen490chrysalis.papilio.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.activities.ActivityRepository
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IActivityApiService
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
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
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.InputStream

@RunWith(JUnit4::class)
class ActivityRepositoryTest
{
    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IUserApiService
    private lateinit var mockRetrofitActivityService : IActivityApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirebaseUserUid = "aset23q45346457sdfhrtu5r"

    private lateinit var activityRepository : IActivityRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp()
    {
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser?.uid).thenReturn(mockFirebaseUserUid)

        mockWebServer.start()
        println("Webserver has successfully started for UserRepository test...")

        mockRetrofitUserService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IUserApiService::class.java)

        mockRetrofitActivityService = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
            .build()
            .create(IActivityApiService::class.java)

        println("Instantiated mockRetrofitUserService for UserRepository test!")

        // Important to initialize the user repository here since the mockRetrofitUserService needs to be create beforehand
        activityRepository =
            Mockito.spy(ActivityRepository(mockFirebaseAuth, mockRetrofitUserService, mockRetrofitActivityService))

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun postNewUserActivity() = runTest {
        val mockServerResponse = MockResponse().setResponseCode(201)
        mockWebServer.enqueue(mockServerResponse)

        val pictures : MutableList<Pair<String, InputStream>> = ArrayList()

        val file = File.createTempFile("temp-file", "_temp")
        pictures.add(Pair("png", file.inputStream()))

        val response = activityRepository.postNewUserActivity(
            "some activity title",
            "some description",
            5,
            10,
            5,
            pictures,
            EventDate(2023, 10, 10),
            EventTime(0, 10),
            EventTime(0, 10),
            "some mapbox address"
        )

        advanceUntilIdle()

        assert(response.isSuccessful && response.code() == 201)
        Mockito.verify(mockFirebaseAuth, times(1)).currentUser
        Mockito.verify(mockFirebaseUser, times(1))?.uid
    }
}