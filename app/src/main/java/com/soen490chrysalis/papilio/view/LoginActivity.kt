package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.soen490chrysalis.papilio.viewModel.LoginViewModel
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.ActivityLoginBinding
import com.soen490chrysalis.papilio.viewModel.AuthResponse
import com.soen490chrysalis.papilio.viewModel.LoginViewModelFactory
import com.soen490chrysalis.papilio.utility.UtilityFunctions


class LoginActivity : AppCompatActivity()
{
    private val logTag = SignUpActivity::class.java.simpleName
    private lateinit var binding : ActivityLoginBinding

    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient : GoogleSignInClient

    private lateinit var loginViewModel : LoginViewModel

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvSignUpNoAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // We cannot place this code in the LoginViewModel because it requires the activity object
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val loginFactory = LoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, loginFactory)[LoginViewModel::class.java]
        loginViewModel.initialize(googleSignInClient)

        val hasUserAuthenticatedObserver = Observer<AuthResponse> { authResponse_ ->
            // The user has successfully logged in. We can now move to the next page/activity
            Log.d(
                logTag,
                "Auth observer has detected changes: \nauth response: ${authResponse_.authSuccessful}"
            )
            if (authResponse_.authSuccessful)
            {
                // Go to main page
                val homePage = Intent(this, MainActivity::class.java)
                startActivity(homePage)
                finish()
            }
            else
            {
                // Display a snackbar with the error message
                val coordinatorLayout = binding.coordinatorLayoutLogin
                displaySnackBar(coordinatorLayout, authResponse_.errorMessage)
            }
        }

        // Register the observer we create above
        loginViewModel.authResponse.observe(this, hasUserAuthenticatedObserver)

        binding.signInWithGoogleButton.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE

            // Initiate the whole Google Sign In procedure
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        binding.loginButton.setOnClickListener {
            // Extract the email and password from the input fields
            val email : String = binding.userEmailAddress.text.toString()
            val password : String = binding.userPassword.text.toString()

            val emailValidation = UtilityFunctions.validateEmailAddress(email)
            if (emailValidation != null)
            {
                binding.userEmailAddress.error = emailValidation
            }

            val passwordValidation = UtilityFunctions.validatePassword(password)
            if (passwordValidation != null)
            {
                binding.userPassword.error = passwordValidation
            }

            if (emailValidation == null && passwordValidation == null)
            {
                binding.progressBarLogin.visibility = View.VISIBLE

                // Valid email and password
                loginViewModel.firebaseLoginWithEmailAndPassword(email, password)
            }
        }
    }

    // Utility function that displays a snackbar in case of errors
    private fun displaySnackBar(coordinatorLayout : CoordinatorLayout, errorMessage : String)
    {
        binding.progressBarLogin.visibility = View.GONE // Hide snackbar in case of errors
        Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            try
            {
                val task : Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)

                // Google Sign In was successful, authenticate with Firebase
                val account : GoogleSignInAccount = task.result
                Log.d(logTag, "firebaseAuthWithGoogle: " + account.id)
                loginViewModel.firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e : Exception)
            {
                // Google Sign In failed, update UI appropriately
                Log.d(logTag, "Google sign in failed: \n" + e.message.toString())

                // Show snackbar with error message
                displaySnackBar(binding.coordinatorLayoutLogin, "Oops, something went wrong!")
            }
        }
    }

}