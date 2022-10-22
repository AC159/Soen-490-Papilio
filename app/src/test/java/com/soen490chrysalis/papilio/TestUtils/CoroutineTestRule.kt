package com.soen490chrysalis.papilio.TestUtils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/*
    DESCRIPTION:
    Coroutine rule for unit or UI tests that call suspend functions.
    This class mocks a coroutine dispatcher such as Dispatchers.IO

    Author: Anastassy Cap
    Date: October 52, 2022
*/

@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher()
{
    override fun starting(description: Description)
    {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description)
    {
        super.finished(description)
        Dispatchers.resetMain()
    }
}