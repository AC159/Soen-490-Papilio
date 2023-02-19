package com.soen490chrysalis.papilio.viewModel

import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.factories.HomeFragmentViewModelFactory
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class HomeFragmentViewModelFactoryTest
{
    @Test
    fun createViewModel()
    {
        // Mock firebase
        val firebaseAuthMock = Mockito.mock(FirebaseAuth::class.java)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuthMock

        val homeFragmentViewModelFactory = HomeFragmentViewModelFactory()
        val homeFragmentViewModel = homeFragmentViewModelFactory.create(HomeFragmentViewModel::class.java)

        println(homeFragmentViewModel.javaClass.simpleName)
        println(HomeFragmentViewModel::class.java.simpleName)

        assert(homeFragmentViewModel.javaClass.simpleName == HomeFragmentViewModel::class.java.simpleName)
    }
}
