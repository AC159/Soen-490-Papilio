package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.soen490chrysalis.papilio.repository.mocks.MockActivityRepository
import com.soen490chrysalis.papilio.services.network.responses.ActivityObject
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import com.soen490chrysalis.papilio.view.dialogs.EventDate
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

@RunWith(JUnit4::class)
class HomeFragmentViewModelTest
{
    private val mockActivityRepository = MockActivityRepository()
    private val homeFragmentViewModel = HomeFragmentViewModel(mockActivityRepository)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllActivities() = runTest {

        homeFragmentViewModel.getAllActivities("1", "5")

        advanceUntilIdle()

        assert(homeFragmentViewModel.activityResponse.value?.rows?.size == 5)

        homeFragmentViewModel.getAllActivities("1", "10")

        advanceUntilIdle()

        assert(homeFragmentViewModel.activityResponse.value?.rows?.size == 10)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun filterSetTest() = runTest {

        var filterValues = homeFragmentViewModel.GetFilterValues()

        assert(filterValues.individualCostRange == mutableListOf(0f, 10000f)
                && filterValues.groupCostRange == mutableListOf(0f, 10000f)
                && filterValues.startDate == homeFragmentViewModel.oldestDate
                && filterValues.endDate == homeFragmentViewModel.furthestDate)

        val individualCostSliderValues = mutableListOf<Float>(20f, 30f)
        val groupCostSliderValues = mutableListOf<Float>(40f, 50f)
        val startDate = EventDate(2023, 5, 20)
        val endDate = EventDate(2023, 5, 22)

        homeFragmentViewModel.SetFilter(
            HomeFragmentViewModel.FilterOptions(
                individualCostSliderValues, groupCostSliderValues, startDate, endDate
            ))

        filterValues = homeFragmentViewModel.GetFilterValues()

        assert(filterValues.individualCostRange == mutableListOf(20f, 30f)
                && filterValues.groupCostRange == mutableListOf(40f, 50f)
                && filterValues.startDate.year == 2023 && filterValues.startDate.month == 5 && filterValues.startDate.day == 20
                && filterValues.endDate.year == 2023 && filterValues.endDate.month == 5 && filterValues.endDate.day == 22)

        homeFragmentViewModel.ResetFilter()

        filterValues = homeFragmentViewModel.GetFilterValues()

        assert(filterValues.individualCostRange == mutableListOf(0f, 10000f)
                && filterValues.groupCostRange == mutableListOf(0f, 10000f)
                && filterValues.startDate == homeFragmentViewModel.oldestDate
                && filterValues.endDate == homeFragmentViewModel.furthestDate)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun filterActivity() = runTest{

        val activityList: MutableList<ActivityObject> = mutableListOf()
        for (i in 1..5) {
            val activityObject = ActivityObject(
                i.toString(),
                "Activity $i Title",
                "This is Activity $i",
                "${i*10}",
                "${i*10}",
                "4",
                listOf("a" + i + "image1", "a" + i + "image1"),
                "2023-05-${19+i}T00:52:16.575Z",
                "2023-05-20T00:52:16.575Z",
                "Activity $i Address",
                "A$i Creation Time",
                "A$i Update Time",
                null,
                "user $i"
            )

            activityList.add(activityObject)
        }

        assert(homeFragmentViewModel.filterActivity(activityList[0])
                && homeFragmentViewModel.filterActivity(activityList[1])
                && homeFragmentViewModel.filterActivity(activityList[2])
                && homeFragmentViewModel.filterActivity(activityList[3])
                && homeFragmentViewModel.filterActivity(activityList[4]))

        val individualCostSliderValues = mutableListOf<Float>(20f, 30f)
        val groupCostSliderValues = mutableListOf<Float>(10f, 50f)
        val startDate = EventDate(2023, 5-1, 20)
        val endDate = EventDate(2023, 5-1, 22)

        homeFragmentViewModel.SetFilter(
            HomeFragmentViewModel.FilterOptions(
                individualCostSliderValues, groupCostSliderValues, startDate, endDate
            ))

        assert(!homeFragmentViewModel.filterActivity(activityList[0])
                && homeFragmentViewModel.filterActivity(activityList[1])
                && homeFragmentViewModel.filterActivity(activityList[2])
                && !homeFragmentViewModel.filterActivity(activityList[3])
                && !homeFragmentViewModel.filterActivity(activityList[4]))

    }
}