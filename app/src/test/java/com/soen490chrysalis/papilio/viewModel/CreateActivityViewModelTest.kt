package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.soen490chrysalis.papilio.repository.mocks.MockActivityRepository
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.InputStream

@RunWith(JUnit4::class)
class CreateActivityViewModelTest
{
    private val mockActivityRepository = MockActivityRepository()
    private val createActivityViewModel = CreateActivityViewModel(mockActivityRepository)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp()
    {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun validateActivityTitle()
    {
        var result = createActivityViewModel.validateActivityTitle("")
        assert(result == "Title must be at least 3 characters long!")

        result = createActivityViewModel.validateActivityTitle("as")
        assert(result == "Title must be at least 3 characters long!")

        result = createActivityViewModel.validateActivityTitle("valid title")
        assert(result == null)
    }

    @Test
    fun validateActivityDescription()
    {
        var result = createActivityViewModel.validateActivityDescription("")
        assert(result == "Description must be at least 15 characters long!")

        result = createActivityViewModel.validateActivityDescription(
            "This is a valid activity description"
        )
        assert(result == null)
    }

    @Test
    fun validateActivityMaxNumberOfParticipants()
    {
        var result =
            createActivityViewModel.validateActivityMaxNumberOfParticipants("not a number!")
        assert(result == "Not a number!")

        result = createActivityViewModel.validateActivityMaxNumberOfParticipants("-1")
        assert(result == "Number of participants must be greater than 0!")

        result = createActivityViewModel.validateActivityMaxNumberOfParticipants("10")
        assert(result == null)
    }

    @Test
    fun validateActivityIndividualCost()
    {
        var result =
            createActivityViewModel.validateActivityIndividualCost("not a number!")
        assert(result == "Not a number!")

        result = createActivityViewModel.validateActivityIndividualCost("-1")
        assert(result == "Number of participants must be greater than or equal to 0!")

        result = createActivityViewModel.validateActivityIndividualCost("5")
        assert(result == null)
    }

    @Test
    fun validateActivityGroupCost()
    {
        var result =
            createActivityViewModel.validateActivityGroupCost("not a number!")
        assert(result == "Not a number!")

        result = createActivityViewModel.validateActivityGroupCost("-1")
        assert(result == "Number of participants must be greater than or equal to 0!")

        result = createActivityViewModel.validateActivityGroupCost("10")
        assert(result == null)
    }

    @Test
    fun validateActivityPictureUris()
    {
        val pictures : MutableList<Pair<String, InputStream>> = ArrayList()
        val mockInputStream = Mockito.mock(InputStream::class.java)
        pictures.add(Pair("png", mockInputStream))

        var result = createActivityViewModel.validateActivityPictureUris(pictures)
        assert(result == null)

        result = createActivityViewModel.validateActivityPictureUris(ArrayList())
        assert(result == "Don't forget to add some pictures!")
    }

    @Test
    fun validateDate()
    {
        var result = createActivityViewModel.validateActivityDate("select date")
        assert(result == "You must select a date!")

        result = createActivityViewModel.validateActivityDate("01/01/2023")
        assert(result == null)
    }

    @Test
    fun validateStartTime()
    {
        val startTime = EventTime(-1, -1)
        var result = createActivityViewModel.validateStartTime(startTime)
        assert(result == "You must select a start time!")

        startTime.hourOfDay = 0
        startTime.minute = 10
        result = createActivityViewModel.validateStartTime(startTime)
        assert(result == null)
    }

    @Test
    fun validateEndTime()
    {
        val endTime = EventTime(-1, -1)
        var result = createActivityViewModel.validateEndTime(endTime)
        assert(result == "You must select an end time!")

        endTime.hourOfDay = 0
        endTime.minute = 10
        result = createActivityViewModel.validateEndTime(endTime)
        assert(result == null)
    }

    @Test
    fun validateActivityAddress()
    {
        val addressSuggestions = ArrayList<String>()
        createActivityViewModel.activityAddressSuggestions.value = addressSuggestions
        var addressValidation = createActivityViewModel.validateActivityAddress()
        assert(addressValidation == "You must select an address from the dropdown!")

        addressSuggestions.add("some mapbox address")
        createActivityViewModel.activityAddressSuggestions.value = addressSuggestions
        addressValidation = createActivityViewModel.validateActivityAddress()

        assert(addressValidation == null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun postNewActivity() = runTest {
        val pictures : MutableList<Pair<String, InputStream>> = ArrayList()
        val mockInputStream = Mockito.mock(InputStream::class.java)
        pictures.add(Pair("png", mockInputStream))

        createActivityViewModel.postNewActivity(
            "Activity title",
            "Some description",
            5,
            10,
            5,
            pictures,
            EventDate(2023, 1, 15),
            EventTime(0, 0),
            EventTime(0, 0)
        )

        advanceUntilIdle()

        println(createActivityViewModel.postNewUserActivityResponse.value)

        assert(
            createActivityViewModel.postNewUserActivityResponse.value?.isSuccess == false
                    && createActivityViewModel.postNewUserActivityResponse.value!!.msg == "Oops, something went wrong!"
        )

        val addressSuggestions = ArrayList<String>()
        addressSuggestions.add("some mapbox address")
        createActivityViewModel.activityAddressSuggestions.value = addressSuggestions

        createActivityViewModel.postNewActivity(
            "Activity title",
            "Some description",
            5,
            10,
            5,
            pictures,
            EventDate(2023, 1, 15),
            EventTime(0, 0),
            EventTime(0, 0)
        )

        advanceUntilIdle()

        assert(
            createActivityViewModel.postNewUserActivityResponse.value?.isSuccess == true
                    && createActivityViewModel.postNewUserActivityResponse.value!!.msg == "Activity successfully created!"
        )
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun getMapBoxAddressSuggestions() = runTest {
//        val query : Query? = Query.create("4 Queen St S, Hamilton, ON L8P 3R3")
//
//        if (query != null)
//        {
//            createActivityViewModel.getMapBoxAddressSuggestions(query)
//        }
//
//        advanceUntilIdle()
//
//        println(createActivityViewModel.activityAddressSuggestions)
//
//        // assert(createActivityViewModel.activityAddressSuggestions.value!!.isNotEmpty())
//    }
}