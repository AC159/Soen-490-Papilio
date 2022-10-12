package com.soen490chrysalis.papilio

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.soen490chrysalis.papilio.view.InitialActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class InitialActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(InitialActivity::class.java)

    @Test
    fun activityDisplaysExpectedText() {
        onView(withText(R.string.papilio_slogan)).check(matches(isDisplayed()))
        onView(withText(R.string.sign_up)).check(matches(isDisplayed()))
        onView(withText(R.string.login)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSignUpActivity() {
        onView(withText(R.string.sign_up)).check(matches(isDisplayed())).perform(click())

        // Check that the sign up activity is displayed
         onView(withText(R.string.signup_activity_greeting_message)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToLoginActivity() {
        onView(withText(R.string.login)).check(matches(isDisplayed())).perform(click())

        // Check that the sign up activity is displayed
        onView(withText(R.string.login_activity_greeting_message)).check(matches(isDisplayed()))
    }
}