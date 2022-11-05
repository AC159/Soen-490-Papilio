package com.soen490chrysalis.papilio.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class LoginViewModelFactoryTest
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

        val loginViewModelFactory = LoginViewModelFactory()
        val loginViewModel = loginViewModelFactory.create(LoginViewModel::class.java)

        println(loginViewModel.javaClass.simpleName)
        println(LoginViewModel::class.java.simpleName)

        assert(loginViewModel.javaClass.simpleName == LoginViewModel::class.java.simpleName)
    }
}