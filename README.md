# Papilio by Chrysalis

## Test Code Coverage for integrated tests

### To generate a Jacoco test report, run the following command in the terminal:

        ./gradlew connectedAndroidTest

The output file will be located in the **./app/build/reports/coverage/androidTest/debug/connected/index.html** directory

## Test Code Coverage for unit tests

### To generate a Jacoco test report, run the following command in the terminal:

        ./gradlew connectedAndroidTest

## Test code coverage for both integrated and unit tests

        ./gradlew jacocoUnifiedTestReport

The output file will be located in the **./app/build/reports/jacoco/jacocoTestReport/html/index.html** directory
