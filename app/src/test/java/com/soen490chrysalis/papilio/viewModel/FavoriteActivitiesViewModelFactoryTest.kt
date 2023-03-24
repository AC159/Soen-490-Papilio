package com.soen490chrysalis.papilio.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.viewModel.factories.FavoriteActivitiesViewModelFactory
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class FavoriteActivitiesViewModelFactoryTest
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

        val favoriteActivitiesViewModelFactory = FavoriteActivitiesViewModelFactory()
        val favoriteActivitiesViewModel =
            favoriteActivitiesViewModelFactory.create(FavoriteActivitiesViewModel::class.java)

        println(favoriteActivitiesViewModel.javaClass.simpleName)
        println(FavoriteActivitiesViewModel::class.java.simpleName)

        assert(favoriteActivitiesViewModel.javaClass.simpleName == FavoriteActivitiesViewModel::class.java.simpleName)
    }
}
