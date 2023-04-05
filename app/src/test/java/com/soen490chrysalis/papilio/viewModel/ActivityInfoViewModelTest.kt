package com.soen490chrysalis.papilio.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.activities.ActivityRepository
import com.soen490chrysalis.papilio.repository.activities.IActivityRepository
import com.soen490chrysalis.papilio.repository.users.CheckActivityMember
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.IActivityApiService
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.services.network.responses.CheckFavoriteResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteResponse
import com.soen490chrysalis.papilio.services.network.responses.FavoriteUserObject
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("PrivatePropertyName")
@RunWith(JUnit4::class)
class ActivityInfoViewModelTest
{
    private lateinit var activityInfoViewModel : ActivityInfoViewModel
    private lateinit var userRepository : IUserRepository
    private lateinit var activityRepository : IActivityRepository

    private var mockWebServer = MockWebServer()
    private lateinit var mockRetrofitUserService : IUserApiService
    private lateinit var mockRetrofitActivityService : IActivityApiService
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockFirebaseUserUid = "aset23q45346457sdfhrtu5r"

    private val activity_id = "160"
    // first value is defines the success of the operation, second term is the message value
    private val success = Pair(true, "")
    private val error = Pair(false, "Oops, something went wrong!")

    private val favResponse = FavoriteResponse(
        true,
        FavoriteUserObject(
            mockFirebaseUserUid,
            "firstName",
            "lastName",
            null,
            null,
            "someValidEmail@gmail.com",
            "Some user bio",
            null
        )
    )

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

        mockRetrofitActivityService = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .baseUrl(mockWebServer.url("/")) // note the URL is different from production one
                .build()
                .create(IActivityApiService::class.java)

        println("Instantiated mockRetrofitUserService for ActivityInfoViewModelTest test!")

        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        Mockito.`when`(mockFirebaseUser.uid).thenReturn(mockFirebaseUserUid)

        // Important to initialize the user repository here since the mockRetrofitUserService needs to be create beforehand
        userRepository = Mockito.spy(UserRepository(mockFirebaseAuth, mockRetrofitUserService))
        activityRepository = Mockito.spy(ActivityRepository(mockFirebaseAuth, mockRetrofitUserService, mockRetrofitActivityService))
        activityInfoViewModel = ActivityInfoViewModel(userRepository, activityRepository)

        println("Setup method is done!")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkActivityMemberTest() = runTest {
        // first value is defines the success of the operation, second term is the message value
        // and the third specifies if a user has joined the specified activity or not
        val success = CheckActivityMember(true, "", true, true)
        val error = CheckActivityMember(false, "Oops, something went wrong!", false, false)
        Mockito.doReturn(success).`when`(userRepository).checkActivityMember(activity_id)

        activityInfoViewModel.checkActivityMember(activity_id)
        advanceUntilIdle()
        activityInfoViewModel.checkActivityMemberResponse.observeForever {
            assert(it!!.isSuccess && it.hasUserJoined && it.errorMessage == "" && it.isUserOwnerOfActivity)
        }

        Mockito.verify(userRepository, times(1)).checkActivityMember(activity_id)

        Mockito.doReturn(error).`when`(userRepository).checkActivityMember(activity_id)
        activityInfoViewModel.checkActivityMember(activity_id)
        advanceUntilIdle()
        activityInfoViewModel.checkActivityMemberResponse.observeForever {
            assert(!it!!.isSuccess && !it.hasUserJoined && it.errorMessage == "Oops, something went wrong!" && !it.isUserOwnerOfActivity)
        }
        Mockito.verify(userRepository, times(2)).checkActivityMember(activity_id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun joinActivityTest() = runTest {
        Mockito.doReturn(success).`when`(userRepository).addUserToActivity(activity_id)

        activityInfoViewModel.joinActivity(activity_id)
        advanceUntilIdle()
        var result = activityInfoViewModel.joinActivityResponse.value
        println("Result: $result")
        assert(result!!.isSuccess && result.errorMessage == "")
        Mockito.verify(userRepository, times(1)).addUserToActivity(activity_id)

        Mockito.doReturn(error).`when`(userRepository).addUserToActivity(activity_id)
        activityInfoViewModel.joinActivity(activity_id)
        advanceUntilIdle()
        result = activityInfoViewModel.joinActivityResponse.value
        assert(!result!!.isSuccess && result.errorMessage == "Oops, something went wrong!")
        Mockito.verify(userRepository, times(2)).addUserToActivity(activity_id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun leaveActivityTest() = runTest {
        Mockito.doReturn(success).`when`(userRepository).removeUserFromActivity(activity_id)

        activityInfoViewModel.leaveActivity(activity_id)
        advanceUntilIdle()
        var result = activityInfoViewModel.leaveActivityResponse.value
        println("Result: $result")
        assert(result!!.isSuccess && result.errorMessage == "")
        Mockito.verify(userRepository, times(1)).removeUserFromActivity(activity_id)

        Mockito.doReturn(error).`when`(userRepository).removeUserFromActivity(activity_id)
        activityInfoViewModel.leaveActivity(activity_id)
        advanceUntilIdle()
        result = activityInfoViewModel.leaveActivityResponse.value
        assert(!result!!.isSuccess && result.errorMessage == "Oops, something went wrong!")
        Mockito.verify(userRepository, times(2)).removeUserFromActivity(activity_id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkActivityFavoritedTest() = runTest {
        val activityFound = CheckFavoriteResponse(true)
        val res = Triple(true, "", activityFound)
        val successResponse = MockResponse().setResponseCode(200).setBody(res.toString())
        Mockito.doReturn(successResponse).`when`(userRepository).isActivityFavorited(activity_id)

        activityInfoViewModel.checkActivityFavorited(Integer.parseInt(activity_id))
        advanceUntilIdle()

        activityInfoViewModel.checkActivityFavoritedResponse.observeForever {
            println("Result: $it")
            assert(it!!.isActivityFound)
        }
        Mockito.verify(userRepository, times(1)).isActivityFavorited(activity_id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addFavoriteActivityTest() = runTest {
        val res = Triple(true, "", favResponse)
        val successResponse = MockResponse().setResponseCode(200).setBody(res.toString())
        Mockito.doReturn(successResponse).`when`(userRepository).addFavoriteActivity(Integer.parseInt(activity_id))

        activityInfoViewModel.addFavoriteActivity(Integer.parseInt(activity_id))
        advanceUntilIdle()

        activityInfoViewModel.activityFavoritedResponse.observeForever {
            println("Result: $it")
            assert(it!!.success == true && it.update == favResponse.update)
        }
        Mockito.verify(userRepository, times(1)).addFavoriteActivity(Integer.parseInt(activity_id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeFavoriteActivityTest() = runTest {
        val res = Triple(true, "", favResponse)
        val successResponse = MockResponse().setResponseCode(200).setBody(res.toString())
        Mockito.doReturn(successResponse).`when`(userRepository).removeFavoriteActivity(Integer.parseInt(activity_id))

        activityInfoViewModel.removeFavoriteActivity(Integer.parseInt(activity_id))
        advanceUntilIdle()

        activityInfoViewModel.activityFavoritedResponse.observeForever {
            println("Result: $it")
            assert(it!!.success == true && it.update == favResponse.update)
        }
        Mockito.verify(userRepository, times(1)).removeFavoriteActivity(Integer.parseInt(activity_id))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setActivityEntryOpenTest() = runTest {
        val res = Pair(200, "OK")
        val successResponse = MockResponse().setResponseCode(200).setBody(res.toString())
        Mockito.doReturn(successResponse).`when`(activityRepository).open(1)

        activityInfoViewModel.SetActivityEntry(1, true)
        advanceUntilIdle()

        activityInfoViewModel.activityEntryResponse.observeForever {
            assert(it!!.first == 200 && it.second == "OK")
        }
        Mockito.verify(activityRepository, times(1)).open(1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setActivityEntryCloseTest() = runTest {
        val res = Pair(200, "OK")
        val successResponse = MockResponse().setResponseCode(200).setBody(res.toString())
        Mockito.doReturn(successResponse).`when`(activityRepository).close(1)

        activityInfoViewModel.SetActivityEntry(1, false)
        advanceUntilIdle()

        activityInfoViewModel.activityEntryResponse.observeForever {
            assert(it!!.first == 200 && it.second == "OK")
        }
        Mockito.verify(activityRepository, times(1)).close(1)
    }
}