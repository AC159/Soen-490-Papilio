package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.soen490chrysalis.papilio.repository.mocks.MockUserRepository
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
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
import java.time.LocalDate

@RunWith(JUnit4::class)
class UpcomingActivitiesViewModelTest
{
    private val mockUserRepository = MockUserRepository()
    private val upcomingActivitiesViewModel = UpcomingActivitiesViewModel(mockUserRepository)

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
    fun getUpcomingActivities() = runTest {

        upcomingActivitiesViewModel.getUpcomingActivities()

        advanceUntilIdle()

        println(upcomingActivitiesViewModel.activitiesResponse.value!!.count)
        assert(upcomingActivitiesViewModel.activitiesResponse.value!!.count == "4")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getActivitiesByMonth() = runTest {

        upcomingActivitiesViewModel.getUpcomingActivities()

        advanceUntilIdle()

        val currentDate = LocalDate.of(2023, 3, 24)
        val chosenMonth = 3

        val result = upcomingActivitiesViewModel.getActivitiesByMonth(chosenMonth, currentDate)

        assert(result.count() == 1)

    }

}