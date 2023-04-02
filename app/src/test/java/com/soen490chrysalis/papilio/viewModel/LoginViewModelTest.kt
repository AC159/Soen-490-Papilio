package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.mocks.MockUserRepository
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/*
    DESCRIPTION:
    Test suite for the login view model

    Author: Anastassy Cap
    Date: October 5, 2022

    Date: October 15, 2022 -> [Added tests for input form validation (first/last names, email, password)]
*/
@RunWith(JUnit4::class)
class LoginViewModelTest
{
    private val mockUserRepository = MockUserRepository()
    private val loginViewModel = LoginViewModel(mockUserRepository)

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
    fun authenticateWithGoogle() = runTest {
        val googleSignIn : GoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)
        loginViewModel.initialize(googleSignIn)

        val mockIdToken = "some random id token for Google to be happy"
        loginViewModel.firebaseAuthWithGoogle(mockIdToken)
        advanceUntilIdle()
        assert(loginViewModel.authResponse.value!!.authSuccessful)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createAccountWithEmailAndPassword() = runTest {
        loginViewModel.firebaseCreateAccountWithEmailAndPassword(
            "firstName",
            "lastName",
            "some email",
            "password"
        )
        advanceUntilIdle()
        assert(loginViewModel.authResponse.value!!.authSuccessful)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loginWithEmailAndPassword() = runTest {
        loginViewModel.firebaseLoginWithEmailAndPassword("some email", "password")
        advanceUntilIdle()
        assert(loginViewModel.authResponse.value!!.authSuccessful)
    }

    @Test
    fun handleAuthResult()
    {
        loginViewModel.handleAuthResult(true, "")
        assert(loginViewModel.authResponse.value!!.authSuccessful && loginViewModel.authResponse.value!!.errorMessage == "")

        loginViewModel.handleAuthResult(false, "Something went wrong!")
        assert(!loginViewModel.authResponse.value!!.authSuccessful && loginViewModel.authResponse.value!!.errorMessage == "Something went wrong!")
    }

    @Test
    fun getCurrentUser()
    {
        assert(loginViewModel.getUser() is FirebaseUser)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getUserByFirebaseId() = runTest {
        loginViewModel.getUserByFirebaseId()
        advanceUntilIdle()
        assert(loginViewModel.userObject.value != null)
        assert(loginViewModel.userObject.value!!.requestIsFinished)
        assert(loginViewModel.userObject.value!!.userObject!!.email == "validEmail@gmail.com")
    }
}