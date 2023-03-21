package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IUserApiService
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class UserChatViewModelTest
{
    private lateinit var userChatViewModel : UserChatViewModel

    private lateinit var userRepository : IUserRepository
    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IUserApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirebaseUserUid = "aset23q45346457sdfhrtu5r"

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setup()
    {
        mockWebServer.start()
        println("Webserver has successfully started for UserChatViewModel test...")

        mockRetrofitUserService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IUserApiService::class.java)

        println("Instantiated mockRetrofitUserService for UserChatViewModel test!")

        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(mockFirebaseUserUid)

        // Important to initialize the user repository here since the mockRetrofitUserService needs to be create beforehand
        userRepository = Mockito.spy(UserRepository(mockFirebaseAuth, mockRetrofitUserService))
        userChatViewModel = UserChatViewModel(userRepository)

        println("Setup method is done!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCurrentFirebaseUserTest() = runTest {
        val firebaseUser = userChatViewModel.getCurrentFirebaseUser()
        assert(firebaseUser == mockFirebaseUser)
        verify(mockFirebaseAuth, times(1)).currentUser
        verify(userRepository, times(1)).getUser()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getChatTokenTest() = runTest {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSnNNVlhnVDJNaE44eGpwVzFOTnZBTXFMQURmMSJ9.N-FhnRWLgkGP6knf_QD7gWgUJ7Fm4wtbKkodAUqSlwU"
        `when`(userRepository.getNewChatTokenForUser(mockFirebaseUserUid)).thenReturn(token)

        userChatViewModel.getNewChatTokenForUser(mockFirebaseUserUid)

        advanceUntilIdle()

        val receivedToken = userChatViewModel.userChatToken.value
        println("Received chat token: $receivedToken")
        verify(userRepository, times(1)).getNewChatTokenForUser(mockFirebaseUserUid)
        assert(receivedToken == token)
    }
}