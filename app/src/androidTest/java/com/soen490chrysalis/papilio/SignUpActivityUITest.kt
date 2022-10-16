package com.soen490chrysalis.papilio

import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.soen490chrysalis.papilio.view.SignUpActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


fun hasNoErrorText(): Matcher<View?>? {
    return object : BoundedMatcher<View?, EditText>(EditText::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has no error text: ")
        }

        override fun matchesSafely(view: EditText): Boolean {
            return view.error == null
        }
    }
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpActivityUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun activityDisplaysExpectedText()
    {
        Espresso.onView(withText(R.string.signup_activity_greeting_message)).check(
            matches(
                isDisplayed()
            )
        )
        Espresso.onView(withText(R.string.continue_with_google)).check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.sign_up)).check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.signup_activity_user_agreement)).check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice)).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun verifyClickableElements()
    {
        Espresso.onView(withText(R.string.continue_with_google)).check(matches(isClickable()))
        Espresso.onView(withText(R.string.sign_up)).check(matches(isClickable()))
        Espresso.onView(withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice)).check(
            matches(isClickable())
        )
    }

    @Test
    fun verifyThatTermsOfUseDialogIsDisplayed()
    {
        Espresso.onView(withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice))
            .check(matches(isDisplayed())).perform(click())

        // Check that dialog is displayed
        Espresso.onView(isRoot()).inRoot(isDialog()).check(matches(isDisplayed()))

        // Check that the dialog has an "Accept" button at the bottom
        Espresso.onView(withText(R.string.alert_dialog_accept)).check(matches(isDisplayed()))

        // clicking on the dialog's accept button should dismiss it
        Espresso.onView(withText(R.string.alert_dialog_accept)).perform(click())

        // the dialog text should not be displayed anymore
        Espresso.onView(isRoot()).inRoot(not(isDialog()))
    }

    @Test
    fun testFirstNameInputField()
    {
        // Fill the first name field and try to login
        Espresso.onView(withId(R.id.user_first_name)).perform(typeText(
            "this first name is too long and should show an error upon clicking the submit button"
        ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // All input fields should also display their respective errors
        Espresso.onView(withId(R.id.user_first_name)).check(matches(hasErrorText("First name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_last_name)).check(matches(hasErrorText("Last name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_email_address)).check(matches(hasErrorText("Not a valid email!")))
        Espresso.onView(withId(R.id.user_password)).check(matches(hasErrorText("Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")))

        // Fill a valid first name and try to login
        Espresso.onView(withId(R.id.user_first_name)).perform(
            clearText(),
            typeText(
            "validFirstName"
            ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // There should be no errors displayed
        Espresso.onView(withId(R.id.user_first_name)).check(matches(hasNoErrorText()))
    }

    @Test
    fun testLastNameInputField()
    {
        // Fill the email name field and try to login
        Espresso.onView(withId(R.id.user_last_name)).perform(typeText(
            "this last name is too long and should show an error upon clicking the submit button"
        ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // All input fields should also display their respective errors
        Espresso.onView(withId(R.id.user_first_name)).check(matches(hasErrorText("First name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_last_name)).check(matches(hasErrorText("Last name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_email_address)).check(matches(hasErrorText("Not a valid email!")))
        Espresso.onView(withId(R.id.user_password)).check(matches(hasErrorText("Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")))

        // Fill a valid last name and try to login
        Espresso.onView(withId(R.id.user_last_name)).perform(
            clearText(),
            typeText(
                "validLastName"
            ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // There should be no errors displayed
        Espresso.onView(withId(R.id.user_last_name)).check(matches(hasNoErrorText()))
    }

    @Test
    fun testEmailInputField()
    {
        // Fill the email field and try to login
        Espresso.onView(withId(R.id.user_email_address)).perform(typeText(
            "invalid email address"
        ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // All input fields should also display their respective errors
        Espresso.onView(withId(R.id.user_first_name)).check(matches(hasErrorText("First name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_last_name)).check(matches(hasErrorText("Last name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_email_address)).check(matches(hasErrorText("Not a valid email!")))
        Espresso.onView(withId(R.id.user_password)).check(matches(hasErrorText("Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")))

        // Fill a valid email and try to login
        Espresso.onView(withId(R.id.user_email_address)).perform(
            clearText(),
            typeText(
                "validEmail@gmail.com"
            ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // There should be no errors displayed
        Espresso.onView(withId(R.id.user_email_address)).check(matches(hasNoErrorText()))
    }

    @Test
    fun testPasswordInputField()
    {
        // Fill the password field and try to login
        Espresso.onView(withId(R.id.user_password)).perform(typeText(
            "invalid password"
        ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // All input fields should also display their respective errors
        Espresso.onView(withId(R.id.user_first_name)).check(matches(hasErrorText("First name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_last_name)).check(matches(hasErrorText("Last name must be between 1 and 25 characters long!")))
        Espresso.onView(withId(R.id.user_email_address)).check(matches(hasErrorText("Not a valid email!")))
        Espresso.onView(withId(R.id.user_password)).check(matches(hasErrorText("Password must contain at least 1 digit, 1 lowercase character, " +
                "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!")))

        // Fill a valid password and try to login
        Espresso.onView(withId(R.id.user_password)).perform(
            clearText(),
            typeText(
                "validPassword123#$"
            ),
            closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(withText(R.string.sign_up)).perform(click())

        // There should be no errors displayed
        Espresso.onView(withId(R.id.user_password)).check(matches(hasNoErrorText()))
    }
}