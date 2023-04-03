package com.soen490chrysalis.papilio.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.genre.GenreRepository
import com.soen490chrysalis.papilio.repository.genre.IGenreRepository
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IGenreApiService
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.times
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class GenreViewModelTest
{
    private lateinit var genreViewModel : GenreViewModel
    private lateinit var userRepository : IUserRepository
    private lateinit var genreRepository : IGenreRepository

    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IUserApiService
    private lateinit var mockRetrofitGenreService : IGenreApiService
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
        println("Webserver has successfully started for ActivityInfoViewModelTest test...")

        mockRetrofitUserService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IUserApiService::class.java)

        mockRetrofitGenreService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IGenreApiService::class.java)

        println("Instantiated mockRetrofitUserService for ActivityInfoViewModelTest test!")

        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser.uid).thenReturn(mockFirebaseUserUid)

        // Important to initialize the user repository here since the mockRetrofitUserService needs to be create beforehand
        userRepository = Mockito.spy(UserRepository(mockFirebaseAuth, mockRetrofitUserService))
        genreRepository = Mockito.spy(GenreRepository(mockRetrofitGenreService))
        genreViewModel = GenreViewModel(genreRepository, userRepository)

        println("Setup method is done!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllGenresTest() = runTest {
        val expectedResponse = Triple(true, "", listOf(
            GenreObject(
                "1234",
                "Drawing",
                null,
                "art",
                "2022-11-14T02:07:02.585Z",
                "2022-11-14T02:07:02.585Z"
            )
        ))

        Mockito.doReturn(expectedResponse).`when`(genreRepository).getAllGenres()
        genreViewModel.getAllGenres()
        advanceUntilIdle()
        genreViewModel.genreObject.observeForever {
            assert(it == expectedResponse.third)
        }
        Mockito.verify(genreRepository, times(1)).getAllGenres()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun submitQuizTest() = runTest {
        val expectedResponse = Pair(1, "")
        val indoor = true
        val outdoor = false
        val genres = intArrayOf(1,3,5,6)
        Mockito.doReturn(expectedResponse).`when`(userRepository).submitQuiz(indoor, outdoor, genres)

        genreViewModel.submitQuiz(true, false, intArrayOf(1,3,5,6))
        advanceUntilIdle()
        genreViewModel.submitQuizResponse.observeForever {
            assert(it == expectedResponse)
        }
        Mockito.verify(userRepository, times(1)).submitQuiz(indoor, outdoor, genres)
    }
}