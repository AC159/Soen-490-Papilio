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
class ActivityHistoryViewModelTest
{
    private val mockUserRepository = MockUserRepository()
    private val activityHistoryViewModel = ActivityHistoryViewModel(mockUserRepository)

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

        activityHistoryViewModel.getActivityHistory()

        advanceUntilIdle()

        println(activityHistoryViewModel.activitiesResponse.value!!.count)
        assert(activityHistoryViewModel.activitiesResponse.value!!.count == "4")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPastActivities() = runTest {

        activityHistoryViewModel.getActivityHistory()

        advanceUntilIdle()

        val currentDate = LocalDate.of(2023, 3, 24)

        val result = activityHistoryViewModel.getPastActivities(currentDate)

        assert(result.count() == 2)

    }

}