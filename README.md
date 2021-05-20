# Integrate Google Play billing library in your android app
This is an example app demonstrates how to integrate Google Play Billing library in an android app.

Step 1 : Fork this project and change to your package name or just create your own new project.

Step 2 : Add Google Play Billing Library dependency to your app's build.gradle file

        
           dependencies {
               def billing_version = "4.0.0"

               implementation "com.android.billingclient:billing:$billing_version"
           }

Step 3 : Click Sync and wait till it completed syncing

Step 4 : Generate a signed APK or android bundle by Clicking "Build" >> Generate signed APK/Bundle

Step 5 : Log in to your Google Play Console

Step 6 : Create new app and publish it

Step 7 : Create new internal test under your Google Play Console >> Your Application >> Release >> Testing >> Internal Testing

Step 8 : Click "Create new release" and upload APK or bundle and add testers. You can add your own email as a tester.

Step 9 : Finally roll out test

Step 10: In Android studio, copy code from SubscriptionActivity.java to your project according to your need or you can clone this project just to test.

Step 11: Import all classes if you see errors by clicking ALT + ENTER

Step 12 : Run this app in your device with your google account. Make a test purchase.

Congratulations! You have successfully integrated Google Play Billing Library into your anroid app.

Note : This is a demo project and you still need to implement code to save purchase token and verify in backend server.





 *Google Play is a trademark of Google Inc.
