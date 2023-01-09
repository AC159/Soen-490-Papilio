package com.soen490chrysalis.papilio.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.*
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.MockUserRepository
import com.soen490chrysalis.papilio.testUtils.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.kotlin.times

/*
    DESCRIPTION:
    Test suite for the user profile view model

    Author: Anas Peerzada
    Date: January 6, 2023
*/

@RunWith(JUnit4::class)
class UserProfileViewModelTest
{
    private val mockUserRepository = MockUserRepository()
    private val userProfileViewModel = UserProfileViewModel(mockUserRepository)
    private val mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    private val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)
    private val mockEmailAuthCredential = Mockito.mock(EmailAuthCredential::class.java)

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

    @Test
    fun addEditedField_CheckIfEmptyAfterAdding() = runTest {
        userProfileViewModel.addEditedField("field name", "field value")
        assert(!userProfileViewModel.isEditedFieldsEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateUserProfile_WithEmptyFieldsMap() = runTest {
        userProfileViewModel.updateUserProfile()
        assert(userProfileViewModel.updateUserResponse.value!!.status == 0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateUserProfile_Correctly() = runTest {
        userProfileViewModel.addEditedField("some field", "some value")
        userProfileViewModel.updateUserProfile()
        advanceUntilIdle()
        assert(userProfileViewModel.updateUserResponse.value!!.status == 200)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateUserPassword() = runTest {

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns (mockFirebaseAuth)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance().currentUser } returns (mockFirebaseUser)

        Mockito.`when`(mockFirebaseUser.email).thenReturn("validEmail@gmail.com")

        mockkStatic(EmailAuthProvider::class)
        every {
            EmailAuthProvider.getCredential(
                "validEmail@gmail.com",
                "old password"
            )
        } returns (mockEmailAuthCredential)

        val mockedTask = mockk<Task<Void>>(relaxed = true)
        every { mockedTask.isSuccessful } returns true
        every { mockedTask.exception } returns null
        Mockito.`when`(mockFirebaseUser.reauthenticate(mockEmailAuthCredential))
                .thenReturn(mockedTask)
        Mockito.`when`(mockFirebaseUser.updatePassword("new password")).thenReturn(mockedTask)

        val slot = slot<OnCompleteListener<Void>>()

        every {
            mockFirebaseUser.reauthenticate(mockEmailAuthCredential).addOnCompleteListener {}
        } answers {
            slot.captured.onComplete(mockedTask)
            mockedTask
        }

        every { mockFirebaseUser.updatePassword("new password").addOnCompleteListener {} } answers {
            slot.captured.onComplete(mockedTask)
            mockedTask
        }

        userProfileViewModel.changeUserPassword("old password", "new password")
        advanceUntilIdle()

        verify(mockFirebaseUser, times(2)).reauthenticate(mockEmailAuthCredential)
        verify(mockFirebaseUser, times(1)).updatePassword("new password")
    }
}
