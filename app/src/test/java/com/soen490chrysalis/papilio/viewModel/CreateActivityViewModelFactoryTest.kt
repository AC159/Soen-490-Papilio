package com.soen490chrysalis.papilio.viewModel

import com.soen490chrysalis.papilio.viewModel.factories.CreateActivityViewModelFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateActivityViewModelFactoryTest
{
    @Test
    fun createViewModel()
    {
        val createActivityViewModelFactory = CreateActivityViewModelFactory()
        val createActivityViewModel = createActivityViewModelFactory.create(CreateActivityViewModel::class.java)

        println(createActivityViewModel.javaClass.simpleName)
        println(CreateActivityViewModel::class.java.simpleName)

        assert(createActivityViewModel.javaClass.simpleName == CreateActivityViewModel::class.java.simpleName)
    }
}
