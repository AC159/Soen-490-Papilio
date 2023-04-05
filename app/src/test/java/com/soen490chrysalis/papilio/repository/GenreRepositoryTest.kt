package com.soen490chrysalis.papilio.repository

import android.util.Log
import com.soen490chrysalis.papilio.repository.genre.GenreRepository
import com.soen490chrysalis.papilio.repository.genre.IGenreRepository
import com.soen490chrysalis.papilio.services.network.IGenreApiService
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
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
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class GenreRepositoryTest
{
    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IGenreApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private lateinit var genreRepository : IGenreRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp()
    {
        mockWebServer.start()
        println("Webserver has successfully started for GenreRepository test...")

        mockRetrofitUserService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IGenreApiService::class.java)

        println("Instantiated mockRetrofitUserService for GenreRepository test!")

        genreRepository = Mockito.spy(GenreRepository(mockRetrofitUserService))

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
    fun getAllGenresTest() = runTest {
        val expectedResponse =
            listOf(
                GenreObject(
                    "1234",
                    "Drawing",
                    null,
                    "art",
                    "2022-11-14T02:07:02.585Z",
                    "2022-11-14T02:07:02.585Z"
                )
            )

        val mockResponse = MockResponse().setResponseCode(200).setBody(
                    "    [" +
                    "       {\n" +
                    "        \"id\": \"1234\",\n" +
                    "        \"name\": \"Drawing\",\n" +
                    "        \"url\": null,\n" +
                    "        \"category\": \"art\",\n" +
                    "        \"createdAt\": \"2022-11-14T02:07:02.585Z\",\n" +
                    "        \"updatedAt\": \"2022-11-14T02:07:02.585Z\"\n" +
                    "       }\n" +
                    "   ]"
        )
        mockWebServer.enqueue(mockResponse)

        val result = genreRepository.getAllGenres()
        println(result)
        assert(result.third.toString() == expectedResponse.toString())
    }
}