package com.soen490chrysalis.papilio.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


@RunWith(JUnit4::class)
class UserRepositoryTest
{
    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockAuthTask = Mockito.mock(Task::class.java)
    private val mockGoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)

    private val userRepository = Mockito.spy(UserRepository(mockFirebaseAuth))
    private val email = "someEmail@gmail.com"
    private val password = "password"

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp()
    {
        userRepository.initialize(mockGoogleSignInClient) // this line is just to get more test coverage

        // Stub firebase authentication functions
        @Suppress("UNCHECKED_CAST")
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(
            mockAuthTask as Task<AuthResult>?
        )
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockAuthTask)

        // Mock all log calls
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun getFirebaseUser()
    {
        userRepository.getUser()
        verify(mockFirebaseAuth, times(1)).currentUser
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun authenticationWithFirebase() = runTest {
        val authResultCallback = { _: Boolean, _: String ->  } // Lambda function that does nothing

        // Create an account with a successful task
        userRepository.firebaseCreateAccountWithEmailAndPassword(email, password, authResultCallback)
        Mockito.verify(mockFirebaseAuth, times(1)).createUserWithEmailAndPassword(email, password)

        userRepository.firebaseLoginWithEmailAndPassword(email, password, authResultCallback)
        Mockito.verify(mockFirebaseAuth, times(1)).signInWithEmailAndPassword(email, password)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callbackHandler() = runTest {
        val authResultSuccessCallback = { authResult: Boolean, _: String -> assert(authResult) }
        val authResultFailureCallback = { authResult: Boolean, _: String -> assert(!authResult) }

        // Test the code when the authentication task is successful
        Mockito.`when`(mockAuthTask.isSuccessful).thenReturn(true)
        @Suppress("UNCHECKED_CAST")
        userRepository.authTaskCompletedCallback(mockAuthTask as Task<AuthResult>, authResultSuccessCallback)
        Mockito.verify(userRepository, times(1)).getUser()

        // Test the code when the authentication task is not successful
        Mockito.`when`(mockAuthTask.isSuccessful).thenReturn(false)
        Mockito.`when`(mockAuthTask.exception).thenReturn(Mockito.mock(Exception::class.java))
        userRepository.authTaskCompletedCallback(mockAuthTask, authResultFailureCallback)
    }
}