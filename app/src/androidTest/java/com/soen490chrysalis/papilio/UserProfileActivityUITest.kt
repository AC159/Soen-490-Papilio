package com.soen490chrysalis.papilio

import android.app.Activity
import android.app.Instrumentation
import android.media.Image
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.soen490chrysalis.papilio.view.AccountMenuFragment
import com.soen490chrysalis.papilio.view.UserProfileActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UserProfileActivityUITest
{
    @get:Rule
    val activityRule = ActivityScenarioRule(UserProfileActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsTestRule(UserProfileActivity::class.java)

    @Before
    fun setUp()
    {
        //Might be filled later so keeping it
    }

    @After
    fun finish()
    {
        //Might be filled later so keeping it
    }

    @Test
    fun confirmAllBasicUserProfileElementsPresentOnScreen()
    {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile_picture)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.edit_profile_button)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_bio)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_personal_info_box)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_email)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun confirmAllEditFieldsAppearWhenEditProfileButtonPressed()
    {
        Espresso.onView(ViewMatchers.withId(R.id.edit_profile_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_bio_edit)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_addPhoneButton)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_passwordButton)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun confirmDialogAppearsWhenChangePasswordButtonPressed()
    {
        Espresso.onView(ViewMatchers.withId(R.id.edit_profile_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_passwordButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Change Password")).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

    @Test
    fun confirmDialogAppearsWhenAddPhoneButtonPressed()
    {
        Espresso.onView(ViewMatchers.withId(R.id.edit_profile_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.user_profile_addPhoneButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Add Phone Number")).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

}