package com.soen490chrysalis.papilio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.databinding.ActivityInitialBinding
import com.soen490chrysalis.papilio.viewModel.LoginViewModel
import com.soen490chrysalis.papilio.viewModel.LoginViewModelFactory

class InitialActivity : AppCompatActivity() {

    private val logTag = InitialActivity::class.java.simpleName
    private lateinit var loginViewModel : LoginViewModel

    private lateinit var binding: ActivityInitialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginFactory = LoginViewModelFactory()
        loginViewModel = ViewModelProvider(this, loginFactory)[LoginViewModel::class.java]

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

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser : FirebaseUser? = loginViewModel.getUser()

        if ( currentUser != null )
        {
            Log.d(logTag, "Current user: ${currentUser.displayName}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}