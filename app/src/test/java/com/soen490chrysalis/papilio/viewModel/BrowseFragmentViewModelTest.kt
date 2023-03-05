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
class BrowseFragmentViewModelTest
{
    private val mockActivityRepository = MockActivityRepository()
    private val browseFragmentViewModel = BrowseFragmentViewModel(mockActivityRepository)

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
    fun searchActivities() = runTest {

        browseFragmentViewModel.searchActivities("keyword1")

        advanceUntilIdle()

        assert(browseFragmentViewModel.activitiesResponse.value!!.keyword == "keyword1")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count == "5")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count.toInt() == browseFragmentViewModel.activitiesResponse.value!!.rows.count())

        browseFragmentViewModel.searchActivities("keyword2")

        advanceUntilIdle()

        assert(browseFragmentViewModel.activitiesResponse.value!!.keyword == "keyword2")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count == "5")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count.toInt() == browseFragmentViewModel.activitiesResponse.value!!.rows.count())

        browseFragmentViewModel.searchActivities("keyword3")

        advanceUntilIdle()

        assert(browseFragmentViewModel.activitiesResponse.value!!.keyword == "keyword3")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count == "5")
        assert(browseFragmentViewModel.activitiesResponse.value!!.count.toInt() == browseFragmentViewModel.activitiesResponse.value!!.rows.count())

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getActivity() = runTest {

        browseFragmentViewModel.getActivity(1)

        advanceUntilIdle()

        assert(browseFragmentViewModel.activityResponse.value!!.found && browseFragmentViewModel.activityResponse.value!!.activity.id?.toInt() == 1)

        browseFragmentViewModel.getActivity(2)

        advanceUntilIdle()

        assert(browseFragmentViewModel.activityResponse.value!!.found && browseFragmentViewModel.activityResponse.value!!.activity.id?.toInt() == 2)

        browseFragmentViewModel.getActivity(156)

        advanceUntilIdle()
        
        assert(browseFragmentViewModel.activityResponse.value!!.found && browseFragmentViewModel.activityResponse.value!!.activity.id?.toInt() == 156)

    }
}