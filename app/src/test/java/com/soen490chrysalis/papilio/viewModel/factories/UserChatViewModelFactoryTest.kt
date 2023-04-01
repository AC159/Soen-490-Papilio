package com.soen490chrysalis.papilio.viewModel.factories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.UserChatViewModel
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class UserChatViewModelFactoryTest
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

        val factory = UserChatViewModelFactory()
        val viewModel = factory.create(UserChatViewModel::class.java)

        println(viewModel.javaClass.simpleName)
        println(UserChatViewModel::class.java.simpleName)

        assert(viewModel.javaClass.simpleName == UserChatViewModel::class.java.simpleName)
    }
}