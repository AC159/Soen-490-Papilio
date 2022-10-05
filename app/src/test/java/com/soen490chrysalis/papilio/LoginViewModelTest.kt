package com.soen490chrysalis.papilio

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.soen490chrysalis.papilio.viewModel.LoginViewModel
import org.junit.Test
import org.mockito.Mockito

/*
    DESCRIPTION:
    Test suite for the login view model

    Author: Anastassy Cap
    Date: October 5, 2022
*/
class LoginViewModelTest
{
    private val mockUserRepository = MockUserRepository()
    private val loginViewModel = LoginViewModel(mockUserRepository)

    @Test
    fun authenticateWithGoogle()
    {
        val googleSignIn : GoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)
        loginViewModel.initialize(googleSignIn)

        val mockIdToken = "some random id token for Google to be happy"
        mockUserRepository.firebaseAuthWithGoogle(mockIdToken) { authResult: Boolean ->
            assert(authResult)
        }
    }

    @Test
    fun failGoogleAuthentication()
    {
        val googleSignIn : GoogleSignInClient = Mockito.mock(GoogleSignInClient::class.java)
        loginViewModel.initialize(googleSignIn)

        val mockIdToken = "some random id token for Google to be happy"
        mockUserRepository.failFirebaseAuthWithGoogle(mockIdToken) { authResult: Boolean ->
            assert(!authResult)
        }
    }

    @Test
    fun getCurrentUser()
    {
        assert(mockUserRepository.getUser() != null)
    }

}