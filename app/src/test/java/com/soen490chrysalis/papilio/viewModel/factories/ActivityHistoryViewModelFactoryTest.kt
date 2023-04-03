package com.soen490chrysalis.papilio.viewModel.factories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.ActivityHistoryViewModel
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class ActivityHistoryViewModelFactoryTest
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

        val factory = ActivityHistoryViewModelFactory()
        val viewModel = factory.create(ActivityHistoryViewModel::class.java)

        println(viewModel.javaClass.simpleName)
        println(ActivityHistoryViewModel::class.java.simpleName)

        assert(viewModel.javaClass.simpleName == ActivityHistoryViewModel::class.java.simpleName)
    }
}