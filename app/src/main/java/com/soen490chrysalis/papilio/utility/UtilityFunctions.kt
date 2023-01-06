package com.soen490chrysalis.papilio.utility

import java.util.regex.Pattern

class UtilityFunctions {
    companion object{
        fun validatePhoneNumber(number : String): String?
        {
            if(number.length < 10)
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

        fun confirmPassword(password : String, password2 : String) : String?
        {
            return if(password == password2)
                null
            else
                "Make sure passwords match!"
        }
    }
}