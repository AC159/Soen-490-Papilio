package com.soen490chrysalis.papilio.viewModel

import android.net.Uri
import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class CreateActivityViewModelTest
{
    private val createActivityViewModel = CreateActivityViewModel()

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
    fun validateActivityTitle()
    {
        var result = createActivityViewModel.validateActivityTitle("")
        assert(result == "Title must be at least 3 characters long!")

        result = createActivityViewModel.validateActivityTitle("as")
        assert(result == "Title must be at least 3 characters long!")

        result = createActivityViewModel.validateActivityTitle("valid title")
        assert(result == null)
    }

    @Test
    fun validateActivityDescription()
    {
        var result = createActivityViewModel.validateActivityDescription("")
        assert(result == "Description must be at least 15 characters long!")

        result = createActivityViewModel.validateActivityDescription(
            "This is a valid activity description"
        )
        assert(result == null)
    }

    @Test
    fun validateActivityMaxNumberOfParticipants()
    {
        var result =
            createActivityViewModel.validateActivityMaxNumberOfParticipants("not a number!")
        assert(result == "Not a number!")

        result = createActivityViewModel.validateActivityMaxNumberOfParticipants("-1")
        assert(result == "Number of participants must be greater than 0!")

        result = createActivityViewModel.validateActivityMaxNumberOfParticipants("10")
        assert(result == null)
    }

    @Test
    fun validateActivityPictureUris()
    {
        val pictures : MutableList<Uri> = ArrayList()
        val mockUri = Mockito.mock(Uri::class.java)
        pictures.add(mockUri)

        var result = createActivityViewModel.validateActivityPictureUris(pictures)
        assert(result == null)

        result = createActivityViewModel.validateActivityPictureUris(ArrayList())
        assert(result == "Don't forget to add some pictures!")
    }
}