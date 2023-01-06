package com.soen490chrysalis.papilio.view

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.*
import com.soen490chrysalis.papilio.databinding.ActivityUserProfileBinding
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository
import com.soen490chrysalis.papilio.services.network.UserApi
import com.soen490chrysalis.papilio.viewModel.UserProfileViewModel


class UserProfileActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityUserProfileBinding
    private lateinit var userProfileViewModel : UserProfileViewModel
    private var isEditing : Boolean = false
    private var isGoogleAccount : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Get the binding for the User Profile Activity
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userRepository : IUserRepository =
            UserRepository(FirebaseAuth.getInstance(), userService = UserApi.retrofitService)

        userProfileViewModel = UserProfileViewModel(userRepository);
        userProfileViewModel.getUserByFirebaseId()

        // Get the last signed in Google account so we can check if the user logged in with Google or through normal log in
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        // If the Google account exists (which means the user used Google to log in / sign up
        if(acct != null)
        {
            isGoogleAccount = true

            // Get the photoUrl of the google profile picture of your Google account
            var googleProfilePicture = acct.photoUrl

            // if the user's google profile picture exists then display it on the user profile activity
            if(googleProfilePicture == null)
            {
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            }
            else // If the user's google profile picture does not exist, then display just the placeholder image on the profile
            {
                // Here we use the Glide library to import the pfp from the google account of the logged in user
                Glide.with(this)
                    .load(acct.photoUrl)
                    .circleCrop()
                    .into(binding.userProfilePicture)
            }

            // Also display the full name and email from the user's Google account on the profile
            binding.userProfileName.setText(acct.displayName)
            binding.userProfileEmail.setText("Email: "+acct.email)

            userProfileViewModel.userObject.observe(this, Observer {
                binding.userProfileBio.setText(userProfileViewModel.userObject.value?.userObject?.bio)
                binding.userProfileBioEdit.setText(userProfileViewModel.userObject.value?.userObject?.bio)
                binding.userProfilePhone.setText("Phone: +"
                        + userProfileViewModel.userObject.value?.userObject?.countryCode
                        + " "
                        + userProfileViewModel.userObject.value?.userObject?.phone)
            })

        }
        else // If the google account does not exist, then it means the user logged in with their firebase account
        {
            // Get the firebase user from FireBaseAuth
            val user = FirebaseAuth.getInstance().currentUser
            user?.let{

                // Set the full name, email on the profile from the firebase account, and show the placeholder pfp as the pfp

                binding.userProfileName.setText(user.displayName)
                binding.userProfileEmail.setText("Email: " + user.email)
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                userProfileViewModel.userObject.observe(this, Observer {
                    binding.userProfileBio.setText(userProfileViewModel.userObject.value?.userObject?.bio)
                    binding.userProfileBioEdit.setText(userProfileViewModel.userObject.value?.userObject?.bio)

                    var countryCode = userProfileViewModel.userObject.value?.userObject?.countryCode
                    var phone = userProfileViewModel.userObject.value?.userObject?.phone

                    if(phone != null)
                    {
                        binding.userProfileAddPhoneButton.visibility = View.GONE
                        binding.userProfilePhoneLayout.visibility = View.VISIBLE
                        binding.userProfilePhone.setText("Phone: +"
                                + countryCode
                                + " "
                                + phone)
                    }
                })
            }
        }

        // Create Action Bar var so we can 1) display it with a proper title and 2) put a working back button on it
        var actionBar = supportActionBar
        super.onCreate(savedInstanceState)

        // if Action Bar is not null, then put a back button on it as well as put the "User Profile" title on it
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "User Profile"
        }

        // listener for the "Edit Profile" button on the user profile page.
        binding.editProfileButton.setOnClickListener{
            if(!isEditing) // if we are not already in editing mode, change the text of the button and make all editable fields visible and available for editing
            {
                binding.editProfileButton.text="Save Changes"
                binding.userProfileBioEdit.visibility = View.VISIBLE
                binding.userProfilePhoneEditButton.visibility = View.VISIBLE
                binding.userProfileBio.visibility = View.GONE
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                if(!isGoogleAccount) // only display the "Change Password" button if the current account is a Google account and not just a database/Firebase account.
                {
                    binding.userProfilePasswordButton.visibility = View.VISIBLE
                }

                isEditing = true // now we are in editing mode
            }
            else { // if we are already in editing mode, then save the changes made by the user to their profile, and call the viewmodel function that sends this info through a PUT request

                val error : String? = null

                if (this.currentFocus != null) {
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                }
                binding.userProfileBioEdit.visibility = View.GONE
                binding.userProfileBio.visibility = View.VISIBLE
                binding.editProfileButton.text="Edit Profile"
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                if(!isGoogleAccount)
                {
                    binding.userProfilePasswordButton.visibility = View.GONE
                }

                isEditing = false // No longer in editing mode because changes were saved

                // The following if-statements are all about adding the edited fields to a map in the view model

                if(binding.userProfileBio.text != binding.userProfileBioEdit.text)
                {
                    userProfileViewModel.addEditedField("bio", binding.userProfileBioEdit.text.toString())
                }


                // If at least one field was edited by the user, then send the PUT request to the backend so that the user's changes can be saved
                if(!userProfileViewModel.isEditedFieldsEmpty())
                {
                    userProfileViewModel.updateUserProfile()
                }
            }
        }

        binding.userProfilePasswordButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Change Password")

            val passwordInput1 = EditText(this)
            passwordInput1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput1.hint = "Enter Current Password"

            val passwordInput2 = EditText(this)
            passwordInput2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput2.hint = "Enter New Password"

            val passwordInput3 = EditText(this)
            passwordInput3.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput3.hint = "Confirm New Password"

            val messageText = TextView(this)
            messageText.setTextColor(Color.RED)

            val layout = LinearLayout(this)

            val params = LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 75, 50, 75)
            layout.layoutParams = params

            layout.addView(passwordInput1)
            layout.addView(passwordInput2)
            layout.addView(passwordInput3)
            layout.addView(messageText)


            builder.setView(layout)

            builder.setPositiveButton(
                "Save",
                DialogInterface.OnClickListener { dialog_ : DialogInterface, _ : Int ->

                })

            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog : DialogInterface, _ : Int ->
                    run {
                        // When the "Accept button is pressed, simply dismiss the dialog
                        dialog.dismiss()
                    }
                })

            val dialog = builder.create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

                var closeDialog = false


                var validationError : String? = null
                var confirmationError : String? = null

                validationError = userProfileViewModel.validatePassword(passwordInput2.text.toString())
                confirmationError = userProfileViewModel.confirmPassword(passwordInput2.text.toString(), passwordInput3.text.toString())

                if(validationError != null)
                {
                    messageText.setText(validationError)
                }
                else if (confirmationError != null)
                {
                    messageText.setText(confirmationError)
                }
                else if(passwordInput1.text == null)
                {
                    messageText.setText("Please enter current password")
                }
                else
                {
                    userProfileViewModel.changeUserPassword(passwordInput1.text.toString(), passwordInput3.text.toString())
                    closeDialog = true
                }

                if (closeDialog) dialog.dismiss()
            })

        }

        binding.userProfileAddPhoneButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Change Password")

            val countryCodeInput = EditText(this)
            countryCodeInput.inputType = InputType.TYPE_CLASS_NUMBER

            val phoneInput = EditText(this)
            phoneInput.inputType = InputType.TYPE_CLASS_PHONE

            val messageText = TextView(this)
            messageText.setTextColor(Color.RED)

            val layout = LinearLayout(this)

            val params = LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 75, 50, 75)
            layout.layoutParams = params

            layout.addView(countryCodeInput)
            layout.addView(phoneInput)
            layout.addView(messageText)

            builder.setView(layout)

            builder.setPositiveButton(
                "Save",
                DialogInterface.OnClickListener { dialog_ : DialogInterface, _ : Int ->

                })

            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog_ : DialogInterface, _ : Int ->
                    run {
                        // When the "Accept button is pressed, simply dismiss the dialog
                        dialog_.dismiss()
                    }
                })

            val dialog = builder.create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

                var closeDialog = false

                val validationError1 = userProfileViewModel.validateCountryCode(countryCodeInput.text.toString())
                val validationError2 = userProfileViewModel.validatePhoneNumber(phoneInput.text.toString())

                if(validationError1 != null)
                {
                    messageText.setText(validationError1)
                }
                else if (validationError2 != null)
                {
                    messageText.setText(validationError2)
                }
                else
                {
                    userProfileViewModel.addEditedField("countryCode", countryCodeInput.text.toString())
                    userProfileViewModel.addEditedField("phone", phoneInput.text.toString())
                    closeDialog = true
                }

                if (closeDialog) dialog.dismiss()
            })

        }

        userProfileViewModel.passwordChangeResult.observe(this, Observer {
            if(userProfileViewModel.passwordChangeResult.value != null)
            {
                val coordinatorLayout = binding.relativeLayoutUserProfile
                displaySnackBar(coordinatorLayout, userProfileViewModel.passwordChangeResult.value.toString())
            }
        })
    }

    // Utility function that displays a snackbar in case of errors
    private fun displaySnackBar(coordinatorLayout : RelativeLayout, errorMessage : String)
    {
        Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    // This is the function that's called when the back button on the action bar is pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun EditText.hideKeyboard() {
        clearFocus()
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(getWindowToken(), 0)
    }
}