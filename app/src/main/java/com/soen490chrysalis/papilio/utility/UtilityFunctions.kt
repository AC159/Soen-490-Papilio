package com.soen490chrysalis.papilio.utility

import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

class UtilityFunctions {
    companion object{
        fun validatePhoneNumber(number : String): String?
        {
            if(number.length != 10)
            {
                return "Please enter a valid phone number (10 digits)"
            }

            return null
        }

        fun validateCountryCode(number : String): String?
        {
            if(number.length > 5 || number.isEmpty())
            {
                return "Please enter a valid country code (without the plus sign)"
            }

            return null
        }

        fun validatePassword(password : String) : String?
        {
            val passwordREGEX = Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +         // at least 1 digit
                        "(?=.*[a-z])" +         // at least 1 lower case letter
                        "(?=.*[A-Z])" +         // at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      // any letter
                        "(?=.*[!@#$%^&*()_+])" +// at least 1 special character
                        "(?=\\S+$)" +           // no white spaces
                        ".{6,}" +               // at least 6 characters
                        "$"
            )

            if (password.length in 6..20 && passwordREGEX.matcher(password).matches())
            {
                return null
            }
            return "Password must contain at least 1 digit, 1 lowercase character, " +
                    "1 uppercase character, 1 special character, no white spaces & a minimum of 6 characters!"
        }

        fun validateFirstName(firstName : String) : String?
        {
            if (firstName.length in 1..25)
            {
                return null
            }
            return "First name must be between 1 and 25 characters long!"
        }

        fun validateLastName(lastName : String) : String?
        {
            if (lastName.length in 1..25)
            {
                return null
            }
            return "Last name must be between 1 and 25 characters long!"
        }

        fun validateEmailAddress(emailAddress : String) : String?
        {
            if (emailAddress.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(emailAddress)
                    .matches()
            )
            {
                return null
            }
            return "Not a valid email!"
        }

        fun confirmPassword(password : String, password2 : String) : String?
        {
            return if(password == password2)
                null
            else
                "Make sure passwords match!"
        }
    }
}