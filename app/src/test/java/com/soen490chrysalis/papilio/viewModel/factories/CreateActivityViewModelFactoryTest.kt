package com.soen490chrysalis.papilio.viewModel.factories

import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.CreateActivityViewModel
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class CreateActivityViewModelFactoryTest
{
    @Test
    fun createViewModel()
    {
        // Mock firebase
        val firebaseAuthMock = Mockito.mock(FirebaseAuth::class.java)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuthMock

        val createActivityViewModelFactory = CreateActivityViewModelFactory()
        val createActivityViewModel =
            createActivityViewModelFactory.create(CreateActivityViewModel::class.java)

        println(createActivityViewModel.javaClass.simpleName)
        println(CreateActivityViewModel::class.java.simpleName)

        assert(createActivityViewModel.javaClass.simpleName == CreateActivityViewModel::class.java.simpleName)
    }
}
