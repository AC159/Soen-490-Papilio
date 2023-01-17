package com.soen490chrysalis.papilio.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import com.soen490chrysalis.papilio.R
import com.soen490chrysalis.papilio.databinding.ActivitySignUpBinding
import com.soen490chrysalis.papilio.utility.UtilityFunctions
import com.soen490chrysalis.papilio.viewModel.AuthResponse
import com.soen490chrysalis.papilio.viewModel.LoginViewModel
import com.soen490chrysalis.papilio.viewModel.factories.LoginViewModelFactory

class SignUpActivity : AppCompatActivity()
{
    private val logTag = SignUpActivity::class.java.simpleName

    private lateinit var binding : ActivitySignUpBinding

    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient : GoogleSignInClient

    private lateinit var loginViewModel : LoginViewModel

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                "Auth observer has detected changes: \nauth response: ${authResponse_.authSuccessful}" +
                        "\nerror message: ${authResponse_.errorMessage}"
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
                val coordinatorLayout = binding.coordinatorLayoutSignUp
                displaySnackBar(coordinatorLayout, authResponse_.errorMessage)
            }
        }

        // Register the observer we create above on the two variables
        loginViewModel.authResponse.observe(this, hasUserAuthenticatedObserver)

        // Register the login with google listener
        binding.continueWithGoogleButton.setOnClickListener {
            binding.progressBarSignUp.visibility = View.VISIBLE

            // Initiate the whole Google Sign In procedure
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        // Register a listener for the terms of use
        binding.tvTermsOfUse.setOnClickListener {
            // Creating an alert ox to display the EULA
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Papilio - EULA") // Give a title to the alert box
            val eulaHtml = getString(R.string.papilio_eula) // fetch the EULA text in HTML form
            val htmlAsSpanned = Html.fromHtml(eulaHtml) // Convert the EULA from HTML form to String
            builder.setMessage(htmlAsSpanned) // Put the huge ass EULA text into the alert box

            // Create a button at the bottom of the alert box called "ACCEPT".
            // This also includes the code that will run when the "ACCEPT" button is pressed.
            builder.setPositiveButton(
                R.string.alert_dialog_accept,
                DialogInterface.OnClickListener { dialog_ : DialogInterface, _ : Int ->
                    run {
                        // When the "Accept button is pressed, simply dismiss the dialog
                        dialog_.dismiss()
                    }
                })

            builder.show() //When the alert box is set up, finally display it on screen.
        }

        // Register listeners for the sign up button
        binding.signUpButton.setOnClickListener {

            val firstName : String = binding.userFirstName.text.toString()
            val lastName : String = binding.userLastName.text.toString()
            val email : String = binding.userEmailAddress.text.toString()
            val password : String = binding.userPassword.text.toString()

            // Take the user input from all the input fields and validate them
            val firstNameValidation = UtilityFunctions.validateFirstName(firstName)
            if (firstNameValidation != null)
            {
                binding.userFirstName.error = firstNameValidation
            }

            val lastNameValidation = UtilityFunctions.validateLastName(lastName)
            if (lastNameValidation != null)
            {
                binding.userLastName.error = lastNameValidation
            }

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

            if (firstNameValidation == null && lastNameValidation == null && emailValidation == null && passwordValidation == null)
            {
                binding.progressBarSignUp.visibility = View.VISIBLE
                loginViewModel.firebaseCreateAccountWithEmailAndPassword(
                    firstName,
                    lastName,
                    email,
                    password
                )
            }
        }

    }

    // Utility function that displays a snackbar in case of errors
    private fun displaySnackBar(coordinatorLayout : CoordinatorLayout, errorMessage : String)
    {
        binding.progressBarSignUp.visibility = View.GONE // Hide the progress bar if there is any error
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
                displaySnackBar(binding.coordinatorLayoutSignUp, "Oops, something went wrong!")
            }
        }
    }

}