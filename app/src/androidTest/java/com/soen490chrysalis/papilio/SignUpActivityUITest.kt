package com.soen490chrysalis.papilio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matchers.not
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.soen490chrysalis.papilio.view.SignUpActivity
import org.junit.Rule
import org.junit.Test

class SignUpActivityUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun activityDisplaysExpectedText()
    {
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_greeting_message)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.continue_with_google)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_user_agreement)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun verifyClickableElements()
    {
        Espresso.onView(ViewMatchers.withText(R.string.continue_with_google)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withText(R.string.sign_up)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice)).check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    @Test
    fun verifyThatTermsOfUseDialogIsDisplayed()
    {
        Espresso.onView(ViewMatchers.withText(R.string.signup_activity_our_terms_of_use_and_privacy_notice))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click())

        // Check that dialog is displayed
        Espresso.onView(isRoot()).inRoot(isDialog()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check that the dialog has an "Accept" button at the bottom
        Espresso.onView(ViewMatchers.withText(R.string.alert_dialog_accept)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // clicking on the dialog's accept button should dismiss it
        Espresso.onView(ViewMatchers.withText(R.string.alert_dialog_accept)).perform(ViewActions.click())

        // the dialog text should not be displayed anymore
        Espresso.onView(isRoot()).inRoot(not(isDialog()))
    }
}