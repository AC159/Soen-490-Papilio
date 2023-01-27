package com.soen490chrysalis.papilio

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.soen490chrysalis.papilio.view.LoginActivity
import com.soen490chrysalis.papilio.view.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityUITest
{
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

    private val validEmail = "validEmail@gmail.com"
    private val validPassword = "validPassword123#$"

    @Before
    fun setUp()
    {
        // Stub the Main activity intent
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, null)

        // Verify that the activity is going to redirect towards the MainActivity
        Intents.intending(IntentMatchers.hasComponent(MainActivity::class.java.name))
                .respondWith(intentResult)
    }

    @Test
    fun activityDisplaysExpectedText()
    {
        Espresso.onView(ViewMatchers.withText(R.string.login_activity_greeting_message)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        Espresso.onView(ViewMatchers.withText(R.string.login_activity_login_to_your_account)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        Espresso.onView(ViewMatchers.withText(R.string.sign_in_with_google)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        Espresso.onView(ViewMatchers.withText(R.string.login)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        Espresso.onView(ViewMatchers.withText(R.string.login_activity_login_no_have_account))
                .perform(scrollTo()).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).perform(scrollTo()).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

    @Test
    fun verifyClickableElements()
    {
        Espresso.onView(ViewMatchers.withText(R.string.sign_in_with_google))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withText(R.string.login))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withText(R.string.sign_up))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    @Test
    fun verifyRedirectionToSignUpActivity()
    {
        Espresso.onView(ViewMatchers.withText(R.string.sign_up))
                .perform(scrollTo(), ViewActions.click())

        // Check that we are on the sign up activity
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_greeting_message))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testEmailInputField()
    {
        // Fill the email field and try to login
        Espresso.onView(ViewMatchers.withId(R.id.user_email_address)).perform(
            ViewActions.typeText(
                "invalid email address"
            ),
            ViewActions.closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(ViewMatchers.withText(R.string.login)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.user_email_address)).check(
            ViewAssertions.matches(
                ViewMatchers.hasErrorText("Not a valid email!")
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.user_password)).check(
            ViewAssertions.matches(
                ViewMatchers.hasErrorText(
                    "Password must contain at least 1 digit, 1 lowercase character, " +
                            "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
                )
            )
        )

        // Fill a valid email and try to login
        Espresso.onView(ViewMatchers.withId(R.id.user_email_address)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(
                "validEmail@gmail.com"
            ),
            ViewActions.closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(ViewMatchers.withText(R.string.login)).perform(ViewActions.click())

        // There should be no errors displayed
        Espresso.onView(ViewMatchers.withId(R.id.user_email_address))
                .check(ViewAssertions.matches(hasNoErrorText()))
    }

    @Test
    fun testPasswordInputField()
    {
        // Fill the password field and try to login
        Espresso.onView(ViewMatchers.withId(R.id.user_password)).perform(
            ViewActions.typeText(
                "invalid password"
            ),
            ViewActions.closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        // Clicking the sign up button should display an error
        Espresso.onView(ViewMatchers.withText(R.string.login)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.user_email_address)).check(
            ViewAssertions.matches(
                ViewMatchers.hasErrorText("Not a valid email!")
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.user_password)).check(
            ViewAssertions.matches(
                ViewMatchers.hasErrorText(
                    "Password must contain at least 1 digit, 1 lowercase character, " +
                            "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
                )
            )
        )

        // Fill a valid password and try to login
        Espresso.onView(ViewMatchers.withId(R.id.user_password)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(
                "validPassword123#$"
            ),
            ViewActions.closeSoftKeyboard() // important to close the keyboard otherwise the sign up button is not visible!
        )

        Espresso.onView(ViewMatchers.withText(R.string.login)).perform(ViewActions.click())

        // There should be no errors displayed
        Espresso.onView(ViewMatchers.withId(R.id.user_password))
                .check(ViewAssertions.matches(hasNoErrorText()))
    }

    @Test
    fun continueWithGoogleCanceledIntent()
    {
        // Stub the google sign in intent to mock a canceled intent
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)

        // Verify that we have the proper google intent, if so, respond with the stub
        Intents.intending(IntentMatchers.toPackage("com.google.android.gms"))
                .respondWith(intentResult)

        Espresso.onView(ViewMatchers.withText(R.string.sign_in_with_google))
                .perform(ViewActions.click())

        // Check if there is a snackbar with an error message
        Espresso.onView(ViewMatchers.withText("Oops, something went wrong!")).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun verifyStartActivityForResult()
    {
        // Stub the Main activity intent
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, null)

        // Verify that the activity is going to redirect towards the MainActivity
        Intents.intending(IntentMatchers.hasComponent(MainActivity::class.java.name))
                .respondWith(intentResult)

        activityRule.scenario.onActivity { activity ->
            activity.startActivityForResult(
                Intent(activity, MainActivity::class.java),
                9001
            ) // the request code is the same as in the signup activity
        }

        /* Delay the thread for 1 second so that the snackbar has time to appear.
           Otherwise, the test will just stop without letting the view show the snackbar */
        Thread.sleep(1000)

        // Check if there is a snackbar
        Espresso.onView(ViewMatchers.withText("Oops, something went wrong!")).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}