
# Papilio by Chrysalis

## Setting up the BACKEND_API_URL environment variable
### To be able to make API requests over the network in a debug environment we must configure a couple of things:

Add the following line to the local.properties file:

        BACKEND_API_URL=http://localhost:1337/api/user/

After starting the local  android emulator type this command in the terminal:

	    adb reverse tcp:1337 tcp:1337

This will forward all requests made on port 1337 of the emulator to port 1337 of the local PC running the backend

## Test Code Coverage for integrated tests

### To generate a Jacoco test report, run the following command in the terminal:

        ./gradlew connectedAndroidTest

The output file will be located in the **./app/build/reports/coverage/androidTest/debug/connected/index.html** directory

## Test Code Coverage for unit tests

### To generate a Jacoco test report, run the following command in the terminal:

        ./gradlew connectedAndroidTest

## Test code coverage for both integrated and unit tests

        ./gradlew jacocoUnifiedTestReport

The output file will be located in the **./app/build/reports/jacoco/jacocoUnifiedTestReport/html/index.html** directory