package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.databinding.ActivityInitialBinding
import com.soen490chrysalis.papilio.viewModel.GetUserResponse
import com.soen490chrysalis.papilio.viewModel.LoginViewModel
import com.soen490chrysalis.papilio.viewModel.LoginViewModelFactory

class InitialActivity : AppCompatActivity()
{
    private val logTag = InitialActivity::class.java.simpleName

    private lateinit var loginViewModel : LoginViewModel

    private lateinit var binding : ActivityInitialBinding

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginFactory = LoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, loginFactory)[LoginViewModel::class.java]

        loginViewModel.userObject.observe(this) { userResponse : GetUserResponse ->
            val currentUser : FirebaseUser? = loginViewModel.getUser()

            /* Verify if the current user exists in firebase and has his info stored in the db,
               if yes, then we can simply redirect to the main activity */
            if (currentUser != null && (userResponse.requestIsFinished && userResponse.userObject != null))
            {
                Log.d(logTag, "Current firebase user: ${currentUser.displayName}")
                Log.d(logTag, "Database user info: ${userResponse.userObject}")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart()
    {
        super.onStart()

        /* Make a network request to determine if the current user already has his
        information stored in the database*/
        loginViewModel.getUserByFirebaseId()
    }
}