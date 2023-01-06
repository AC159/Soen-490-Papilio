package com.soen490chrysalis.papilio.view

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.*
import com.soen490chrysalis.papilio.databinding.ActivityUserProfileBinding
import com.soen490chrysalis.papilio.viewModel.UserProfileViewModel
import com.soen490chrysalis.papilio.viewModel.UserProfileViewModelFactory
import com.soen490chrysalis.papilio.utility.UtilityFunctions


class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var userProfileViewModel: UserProfileViewModel
    private var isEditing: Boolean = false
    private var isGoogleAccount: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        //Get the binding for the User Profile Activity
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userProfileFactory = UserProfileViewModelFactory()
        userProfileViewModel =
            ViewModelProvider(this, userProfileFactory)[UserProfileViewModel::class.java]
        userProfileViewModel.getUserByFirebaseId()

        // Get the providerID from the firebase user object so we can check if the user logged in with Google or via email/password
        val user = FirebaseAuth.getInstance().currentUser

        // If the Google account exists (which means the user used Google to log in / sign up
        if (user?.providerId == "google.com") {
            // Need this to access the profile pic url for the user's Google account
            val acct = GoogleSignIn.getLastSignedInAccount(this)

            // Get the photoUrl of the google profile picture of your Google account
            var googleProfilePicture = acct?.photoUrl

            // if the user's google profile picture exists then display it on the user profile activity
            if (googleProfilePicture == null) {
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)
            } else // If the user's google profile picture does not exist, then display just the placeholder image on the profile
            {
                // Here we use the Glide library to import the pfp from the google account of the logged in user
                Glide.with(this)
                    .load(acct?.photoUrl)
                    .circleCrop()
                    .into(binding.userProfilePicture)
            }

            // Also display the full name and email from the user's Google account on the profile
            binding.userProfileName.text = user.displayName
            binding.userProfileEmail.text = "Email: " + user.email

            userProfileViewModel.userObject.observe(this, Observer {
                binding.userProfileBio.text = userProfileViewModel.userObject.value?.userObject?.bio
                binding.userProfileBioEdit.setText(userProfileViewModel.userObject.value?.userObject?.bio)
                val countryCode = userProfileViewModel.userObject.value?.userObject?.countryCode
                val phone = userProfileViewModel.userObject.value?.userObject?.phone

                if (phone != null) {
                    binding.userProfileAddPhoneButton.visibility = View.GONE
                    binding.userProfilePhoneLayout.visibility = View.VISIBLE
                    binding.userProfilePhone.text = ("Phone: +"
                            + countryCode
                            + " "
                            + phone)
                }
            })

        } else // If the google account does not exist, then it means the user logged in with their firebase account
        {
            // Get the firebase user from FireBaseAuth
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {

                // Set the full name, email on the profile from the firebase account, and show the placeholder pfp as the pfp

                binding.userProfileName.text = user.displayName
                binding.userProfileEmail.text = "Email: " + user.email
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                userProfileViewModel.userObject.observe(this, Observer {
                    binding.userProfileBio.text =
                        userProfileViewModel.userObject.value?.userObject?.bio
                    binding.userProfileBioEdit.setText(userProfileViewModel.userObject.value?.userObject?.bio)

                    val countryCode = userProfileViewModel.userObject.value?.userObject?.countryCode
                    val phone = userProfileViewModel.userObject.value?.userObject?.phone

                    if (phone != null) {
                        binding.userProfileAddPhoneButton.visibility = View.GONE
                        binding.userProfilePhoneLayout.visibility = View.VISIBLE
                        binding.userProfilePhone.text = ("Phone: +"
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
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "User Profile"
        }

        // listener for the "Edit Profile" button on the user profile page.
        binding.editProfileButton.setOnClickListener {
            if (!isEditing) // if we are not already in editing mode, change the text of the button and make all editable fields visible and available for editing
            {
                binding.editProfileButton.text = "Save Changes"
                binding.userProfileBioEdit.visibility = View.VISIBLE
                binding.userProfilePhoneEditButton.visibility = View.VISIBLE
                binding.userProfileBio.visibility = View.GONE
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                // only display the "Change Password" button if the current account is not a Google account.
                if (FirebaseAuth.getInstance().currentUser?.providerId != "google.com") {
                    binding.userProfilePasswordButton.visibility = View.VISIBLE
                }

                isEditing = true // now we are in editing mode
            } else { // if we are already in editing mode, then save the changes made by the user to their profile, and call the viewmodel function that sends this info through a PUT request

                if (this.currentFocus != null) {
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                }
                binding.userProfileBioEdit.visibility = View.GONE
                binding.userProfileBio.visibility = View.VISIBLE
                binding.editProfileButton.text = "Edit Profile"
                binding.userProfilePicture.setImageResource(R.drawable.user_pfp_example)

                if (user?.providerId != "google.com") {
                    binding.userProfilePasswordButton.visibility = View.GONE
                }

                isEditing = false // No longer in editing mode because changes were saved

                // The following if-statements are all about adding the edited fields to a map in the view model

                if (binding.userProfileBio.text != binding.userProfileBioEdit.text) {
                    userProfileViewModel.addEditedField(
                        "bio",
                        binding.userProfileBioEdit.text.toString()
                    )
                }

                // If at least one field was edited by the user, then send the PUT request to the backend so that the user's changes can be saved
                if (!userProfileViewModel.isEditedFieldsEmpty()) {
                    userProfileViewModel.updateUserProfile()
                }
            }
        }

        binding.userProfilePasswordButton.setOnClickListener {

            val currentPasswordInput = EditText(this)
            currentPasswordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            currentPasswordInput.hint = "Enter Current Password"

            val newPasswordInput = EditText(this)
            newPasswordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            newPasswordInput.hint = "Enter New Password"

            val newPasswordConfirmInput = EditText(this)
            newPasswordConfirmInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            newPasswordConfirmInput.hint = "Confirm New Password"

            val messageText = TextView(this)
            messageText.setTextColor(Color.RED)

            val layout = LinearLayoutCreator(
                arrayOf(
                    currentPasswordInput,
                    newPasswordInput,
                    newPasswordConfirmInput,
                    messageText
                )
            )
            val builder = DialogBuilderCreator("Change Password", layout)

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

                var closeDialog = false

                var passwordQualityCheck: String? =
                    UtilityFunctions.validatePassword(newPasswordInput.text.toString())
                var passwordEqualityCheck: String? = UtilityFunctions.confirmPassword(
                    newPasswordInput.text.toString(),
                    newPasswordConfirmInput.text.toString()
                )

                if (passwordQualityCheck != null) {
                    messageText.text = passwordQualityCheck
                } else if (passwordEqualityCheck != null) {
                    messageText.text = passwordEqualityCheck
                } else if (currentPasswordInput.text == null) {
                    messageText.text = "Please enter current password"
                } else {
                    userProfileViewModel.changeUserPassword(
                        currentPasswordInput.text.toString(),
                        newPasswordConfirmInput.text.toString()
                    )
                    closeDialog = true
                }

                if (closeDialog) dialog.dismiss()
            })

        }

        val addPhoneNumberFunction = View.OnClickListener {
            val countryCodeInput = EditText(this)
            countryCodeInput.inputType = InputType.TYPE_CLASS_NUMBER
            countryCodeInput.filters = arrayOf(InputFilter.LengthFilter(5))
            countryCodeInput.hint = "Enter Country Code"

            val phoneInput = EditText(this)
            phoneInput.inputType = InputType.TYPE_CLASS_PHONE
            phoneInput.filters = arrayOf(InputFilter.LengthFilter(10))
            phoneInput.hint = "Enter 10-digit phone number"

            val messageText = TextView(this)
            messageText.setTextColor(Color.RED)

            val layout = LinearLayoutCreator(arrayOf(countryCodeInput, phoneInput, messageText))
            val builder = DialogBuilderCreator("Add Phone Number", layout)
            builder.setView(layout)

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

                var closeDialog = false

                val countryCodeValidationCheck =
                    UtilityFunctions.validateCountryCode(countryCodeInput.text.toString())
                val phoneNumberValidationCheck =
                    UtilityFunctions.validatePhoneNumber(phoneInput.text.toString())

                if (countryCodeValidationCheck != null) {
                    messageText.setText(countryCodeValidationCheck)
                } else if (phoneNumberValidationCheck != null) {
                    messageText.setText(phoneNumberValidationCheck)
                } else {
                    userProfileViewModel.addEditedField(
                        "countryCode",
                        countryCodeInput.text.toString()
                    )
                    userProfileViewModel.addEditedField("phone", phoneInput.text.toString())
                    closeDialog = true
                }

                if (closeDialog) dialog.dismiss()
            })
        }

        binding.userProfileAddPhoneButton.setOnClickListener(addPhoneNumberFunction)
        binding.userProfilePhoneEditButton.setOnClickListener(addPhoneNumberFunction)

        binding.userProfilePicture.setOnClickListener {
            if (isEditing && user?.providerId != "google.com") // if we are not already in editing mode, change the text of the button and make all editable fields visible and available for editing
            {
                ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .galleryOnly()
                    .start()
            }
        }

        userProfileViewModel.passwordChangeResult.observe(this, Observer {
            if (userProfileViewModel.passwordChangeResult.value != null) {
                val coordinatorLayout = binding.relativeLayoutUserProfile
                displaySnackBar(
                    coordinatorLayout,
                    userProfileViewModel.passwordChangeResult.value.toString()
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val rootLayout = binding.relativeLayoutUserProfile

        if (resultCode == Activity.RESULT_OK) {

            val uri: Uri = data?.data!!

            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.userProfilePicture)

            displaySnackBar(
                rootLayout,
                "Profile Picture Successfully Changed!"
            )

        } else if (resultCode == ImagePicker.RESULT_ERROR) {

            displaySnackBar(
                rootLayout,
                ImagePicker.getError(data)
            )
        } else {

            displaySnackBar(
                rootLayout,
                "Cancelled"
            )
        }
    }

    // Utility function that displays a snackbar in case of errors
    private fun displaySnackBar(coordinatorLayout: RelativeLayout, errorMessage: String) {
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

    fun LinearLayoutCreator(viewsToAdd: Array<View>): LinearLayout {
        val params = LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val subViewParams = LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val newLayout = LinearLayout(this)
        newLayout.layoutParams = params
        for (view in viewsToAdd) {
            view.layoutParams = subViewParams
            newLayout.addView(view)
        }
        newLayout.orientation = LinearLayout.VERTICAL
        newLayout.setPadding(50, 75, 50, 75)

        return newLayout
    }

    fun DialogBuilderCreator(title: String, layout: LinearLayout): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setView(layout)
        builder.setPositiveButton(
            "Save",
            DialogInterface.OnClickListener { dialog_: DialogInterface, _: Int ->

            })

        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                run {
                    // When the "Accept button is pressed, simply dismiss the dialog
                    dialog.dismiss()
                }
            })

        return builder
    }
}