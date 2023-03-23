package com.soen490chrysalis.papilio.viewModel

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.soen490chrysalis.papilio.viewModel.factories.CreateActivityViewModelFactory
import io.mockk.every
import io.mockk.mockk
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

        // Mock FirebaseApp
        val firebaseAppMock = mockk<FirebaseApp>()
        mockkStatic(FirebaseApp::class)
        every { FirebaseApp.initializeApp(any()) } returns firebaseAppMock

        // Mock FirebasePerformance and Trace
        val firebasePerformanceMock = mockk<FirebasePerformance>()
        val traceMock = mockk<Trace>()

        // Mock the Firebase.performance.newTrace() method to return the mock Trace object
        every { firebasePerformanceMock.newTrace(any()) } returns traceMock

        // Mock the Trace.start() and Trace.stop() methods to return a dummy value
        every { traceMock.start() } returns mockk()
        every { traceMock.stop() } returns mockk()

        // Mock the getInstance method of FirebasePerformance to return the mock instance
        every { FirebasePerformance.getInstance() } returns firebasePerformanceMock

        val createActivityViewModelFactory = CreateActivityViewModelFactory()
        val createActivityViewModel = createActivityViewModelFactory.create(CreateActivityViewModel::class.java)

        println(createActivityViewModel.javaClass.simpleName)
        println(CreateActivityViewModel::class.java.simpleName)

        assert(createActivityViewModel.javaClass.simpleName == CreateActivityViewModel::class.java.simpleName)
    }
}
