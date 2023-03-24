package com.soen490chrysalis.papilio.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.factories.UpcomingActivitiesViewModelFactory
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class UpcomingActivitiesViewModelFactoryTest
{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun createViewModel()
    {
        // Mock firebase
        val firebaseAuthMock = Mockito.mock(FirebaseAuth::class.java)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuthMock

        val upcomingActivitiesViewModelFactory = UpcomingActivitiesViewModelFactory()
        val upcomingActivitiesViewModel =
            upcomingActivitiesViewModelFactory.create(UpcomingActivitiesViewModel::class.java)

        println(upcomingActivitiesViewModel.javaClass.simpleName)
        println(UpcomingActivitiesViewModel::class.java.simpleName)

        assert(upcomingActivitiesViewModel.javaClass.simpleName == UpcomingActivitiesViewModel::class.java.simpleName)
    }
}
