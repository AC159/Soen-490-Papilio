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
class UserProfileViewModelFactoryTest
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

        val userProfileViewModelFactory = UserProfileViewModelFactory()
        val userProfileViewModel =
            userProfileViewModelFactory.create(UserProfileViewModel::class.java)

        println(userProfileViewModel.javaClass.simpleName)
        println(userProfileViewModel::class.java.simpleName)

        assert(userProfileViewModel.javaClass.simpleName == UserProfileViewModel::class.java.simpleName)
    }
}