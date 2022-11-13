package com.soen490chrysalis.papilio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.soen490chrysalis.papilio.view.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUITest
{
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Before
    fun setUp()
    {
        //Might be filled later so keeping it
    }

    @Test
    fun confirmNavBarPresence()
    {
        Espresso.onView(ViewMatchers.withId(R.id.bottomnav)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkNavBarButtons()
    {
        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.home_fragment)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.browse)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.browse_fragment)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.activities)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.activities_fragment)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.account_menu_fragment)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkAccountMenuButtons()
    {
        Espresso.onView(ViewMatchers.withId(R.id.account)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.account_user_profile)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account_activity_quiz)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account_settings)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account_help)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account_about)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.account_logout)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}