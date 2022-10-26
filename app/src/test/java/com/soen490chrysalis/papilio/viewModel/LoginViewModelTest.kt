package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.MockUserRepository
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
        loginViewModel.firebaseCreateAccountWithEmailAndPassword("some email", "password")
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
    fun getCurrentUser()
    {
        assert(loginViewModel.getUser() is FirebaseUser)
    }

    @Test
    fun validateFirstName()
    {
        // Test empty first name
        var res = loginViewModel.validateFirstName("")
        assert(res == "First name must be between 1 and 25 characters long!")

        // Test first name with 26 letters
        res = loginViewModel.validateFirstName("abcdefghijklmnopqrstuvwxyz")
        assert(res == "First name must be between 1 and 25 characters long!")

        // Test first name with 1 letter
        res = loginViewModel.validateFirstName("a")
        assert(res == null)

        // Test valid first name
        res = loginViewModel.validateFirstName("Socrates")
        assert(res == null)
    }

    @Test
    fun validateLastName()
    {
        // Test empty last name
        var res = loginViewModel.validateLastName("")
        assert(res == "Last name must be between 1 and 25 characters long!")

        // Test last name with 26 letters
        res = loginViewModel.validateLastName("abcdefghijklmnopqrstuvwxyz")
        assert(res == "Last name must be between 1 and 25 characters long!")

        // Test last name with 1 letter
        res = loginViewModel.validateLastName("a")
        assert(res == null)

        // Test valid last name
        res = loginViewModel.validateLastName("Machiavelli")
        assert(res == null)
    }

    @Test
    fun validateEmailAddress()
    {
        // Test empty email
        var res = loginViewModel.validateEmailAddress("")
        assert(res == "Not a valid email!")

        // Test valid email
        res = loginViewModel.validateEmailAddress("someValidEmail@gmail.com")
        assert(res == null)
    }

    @Test
    fun validatePassword()
    {
        // Test empty password
        var res = loginViewModel.validatePassword("")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // Test valid password with no special character
        res = loginViewModel.validatePassword("noSpecialChar123")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // password with no digits
        res = loginViewModel.validatePassword("no#Digit_asga")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // password with no letters
        res = loginViewModel.validatePassword("1232423455346")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // Password contains whitespace
        res = loginViewModel.validatePassword("Password hasWhiteSpace*123")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // password too short
        res = loginViewModel.validatePassword("short")
        assert(res == "Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")

        // valid password
        res = loginViewModel.validatePassword("ValidPasswd123$%")
        assert(res == null)
    }

}