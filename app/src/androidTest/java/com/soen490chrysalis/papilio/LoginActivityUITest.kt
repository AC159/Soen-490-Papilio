package com.soen490chrysalis.papilio

import android.text.Html
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun activityDisplaysASignInWithGoogleButton() {
        onView(withText(R.string.sign_in_with_google)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun clickingGoogleSignInButtonShouldDisplayDialogWithUserAgreements() {
        onView(withText(R.string.sign_in_with_google)).check(matches(isCompletelyDisplayed())).perform(click())

        // Check that dialog is displayed
         onView(isRoot()).inRoot(isDialog()).check(matches(isDisplayed()))

        // Check that the dialog has an 'ACCEPT' button
        onView(withText(R.string.alert_dialog_accept)).check(matches(isCompletelyDisplayed()))
    }
}