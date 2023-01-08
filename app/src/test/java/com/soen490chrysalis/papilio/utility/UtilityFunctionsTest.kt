package com.soen490chrysalis.papilio.utility

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import com.soen490chrysalis.papilio.utility.UtilityFunctions //This might look unused but trust me, it's getting used.
import org.junit.Before
import org.junit.Test

/*
    DESCRIPTION:
    Test suite for the general-purpose companion object class UtilityFunctions that
    contains general-purpose functions for a variety of purposes across all views

    Since this class doesn't have any external communication (like database or backend calls), I am not mocking the UtilityFunctions class.
    I will be just using the class directly and accessing its functions directly.

    Author: Anas Peerzada
    Date: January 7, 2023
*/

class UtilityFunctionsTest {

    @Before
    fun setUp()
    {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun validatePhoneNumber(){
        // phone number too short
        var numberTest = UtilityFunctions.validatePhoneNumber("123")
        assert(numberTest == "Please enter a valid phone number (10 digits)")

        // phone number too long
        numberTest = UtilityFunctions.validatePhoneNumber("1234567891011")
        assert(numberTest == "Please enter a valid phone number (10 digits)")

        // phone number perfect length (10 digits)
        numberTest = UtilityFunctions.validatePhoneNumber("1234567890")
        assert(numberTest == null)
    }

    @Test
    fun validateCountryCode_MoreThan5Characters(){
        // country code too big
        var countryCodeTest = UtilityFunctions.validateCountryCode("123456")
        assert(countryCodeTest == "Please enter a valid country code (without the plus sign)")

        // country code empty
        countryCodeTest = UtilityFunctions.validateCountryCode("")
        assert(countryCodeTest == "Please enter a valid country code (without the plus sign)")

        // country code exactly 5 characters (which is the maximum acceptable length of the country code)
        countryCodeTest = UtilityFunctions.validateCountryCode("12345")
        assert(countryCodeTest == null)

        // country code 1 character (correct)
        countryCodeTest = UtilityFunctions.validateCountryCode("1")
        assert(countryCodeTest == null)

        // country code 2 characters (correct)
        countryCodeTest = UtilityFunctions.validateCountryCode("12")
        assert(countryCodeTest == null)

        // country code 3 characters (correct)
        countryCodeTest = UtilityFunctions.validateCountryCode("123")
        assert(countryCodeTest == null)

        // country code 4 characters (correct)
        countryCodeTest = UtilityFunctions.validateCountryCode("1234")
        assert(countryCodeTest == null)

    }

    @Test
    fun validatePassword()
    {
        // Test empty password
        var res = UtilityFunctions.validatePassword("")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // Test valid password with no special character
        res = UtilityFunctions.validatePassword("noSpecialChar123")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // password with no digits
        res = UtilityFunctions.validatePassword("no#Digit_asga")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // password with no letters
        res = UtilityFunctions.validatePassword("1232423455346")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // Password contains whitespace
        res = UtilityFunctions.validatePassword("Password hasWhiteSpace*123")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // password too short
        res = UtilityFunctions.validatePassword("short")
        assert(
            res == "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        )

        // valid password
        res = UtilityFunctions.validatePassword("ValidPasswd123$%")
        assert(res == null)
    }

    @Test
    fun validateFirstName()
    {
        // Test empty first name
        var res = UtilityFunctions.validateFirstName("")
        assert(res == "First name must be between 1 and 25 characters long!")

        // Test first name with 26 letters
        res = UtilityFunctions.validateFirstName("abcdefghijklmnopqrstuvwxyz")
        assert(res == "First name must be between 1 and 25 characters long!")

        // Test first name with 1 letter
        res = UtilityFunctions.validateFirstName("a")
        assert(res == null)

        // Test valid first name
        res = UtilityFunctions.validateFirstName("Socrates")
        assert(res == null)
    }

    @Test
    fun validateLastName()
    {
        // Test empty last name
        var res = UtilityFunctions.validateLastName("")
        assert(res == "Last name must be between 1 and 25 characters long!")

        // Test last name with 26 letters
        res = UtilityFunctions.validateLastName("abcdefghijklmnopqrstuvwxyz")
        assert(res == "Last name must be between 1 and 25 characters long!")

        // Test last name with 1 letter
        res = UtilityFunctions.validateLastName("a")
        assert(res == null)

        // Test valid last name
        res = UtilityFunctions.validateLastName("Machiavelli")
        assert(res == null)
    }

    @Test
    fun validateEmailAddress()
    {
        // Test empty email
        var res = UtilityFunctions.validateEmailAddress("")
        assert(res == "Not a valid email!")

        // Test valid email
        res = UtilityFunctions.validateEmailAddress("someValidEmail@gmail.com")
        assert(res == null)
    }

    @Test
    fun confirmPassword()
    {
        // Two different passwords (Bad!)
        var res = UtilityFunctions.confirmPassword("tom", "jerry")
        assert(res == "Make sure passwords match!")

        // Same passwords (Good!)
        res = UtilityFunctions.confirmPassword("jerry", "jerry")
        assert(res == null)
    }
    
    

}