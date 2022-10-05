package com.soen490chrysalis.papilio.viewModel

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel()
{
    var loginSuccessful : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    fun initialize(googleSignInClient : GoogleSignInClient)
    {
        this.googleSignInClient = googleSignInClient

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun getUser() : FirebaseUser?
    {
        return firebaseAuth.currentUser
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authTask : Task<AuthResult> = firebaseAuth.signInWithCredential(credential)

        authTask.addOnCompleteListener { task ->
            Log.d(Log.DEBUG.toString(), "Auth task has finished: $task")

            if (task.isSuccessful)
            {
                // Sign in success, update UI with the signed-in user's information
                Log.d(Log.DEBUG.toString(), "signInWithCredential:success")
                val user = firebaseAuth.currentUser
                if (user != null) {
                    Log.d(Log.DEBUG.toString(), "Current user: ${user.email}")
                }

                /* This will trigger the observer in the LoginActivity which will redirect the user
                  to a new page */
                loginSuccessful.value = true

            }
            else
            {
                // If sign in fails, display a message to the user.
                Log.w(Log.DEBUG.toString(), "signInWithCredential:failure", task.exception)
            }
        }
    }

}