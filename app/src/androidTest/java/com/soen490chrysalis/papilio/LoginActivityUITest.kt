package com.soen490chrysalis.papilio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.soen490chrysalis.papilio.view.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginActivityUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun activityDisplaysExpectedText()
    {
        Espresso.onView(ViewMatchers.withText(R.string.login_activity_greeting_message)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.login_activity_login_to_your_account)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.sign_in_with_google)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.login)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.login_activity_login_no_have_account)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun verifyClickableElements()
    {
        Espresso.onView(ViewMatchers.withText(R.string.sign_in_with_google)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withText(R.string.login)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    @Test
    fun verifyRedirectionToSignUpActivity()
    {
        ViewActions.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).perform(ViewActions.click())

        // Check that we are on the sign up activity
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_greeting_message)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}