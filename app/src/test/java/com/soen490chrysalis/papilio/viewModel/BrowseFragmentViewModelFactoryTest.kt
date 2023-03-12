package com.soen490chrysalis.papilio.viewModel

import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.factories.BrowseFragmentViewModelFactory
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class BrowseFragmentViewModelFactoryTest
{
    @Test
    fun createViewModel()
    {
        // Mock firebase
        val firebaseAuthMock = Mockito.mock(FirebaseAuth::class.java)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuthMock

        val BrowseFragmentViewModelFactory = BrowseFragmentViewModelFactory()
        val BrowseFragmentViewModel =
            BrowseFragmentViewModelFactory.create(BrowseFragmentViewModel::class.java)

        println(BrowseFragmentViewModel.javaClass.simpleName)
        println(BrowseFragmentViewModel::class.java.simpleName)

        assert(BrowseFragmentViewModel.javaClass.simpleName == BrowseFragmentViewModel::class.java.simpleName)
    }
}
