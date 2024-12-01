package com.example.halalify

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule

@RunWith(AndroidJUnit4::class)
@MediumTest
class ScanFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    private lateinit var idlingResource: CountingIdlingResource
    @Before
    fun setUp() {
        // Launch the activity that hosts the fragment
        ActivityScenario.launch(MainActivity::class.java)

        // Switch to the Scan fragment
        onView(withId(R.id.Scan)).perform(click())

        idlingResource = CountingIdlingResource("Camera Permission Request")
    }

    @Test
    fun testFragmentIsDisplayed() {
        // Check if the ScanFragment is displayed
        onView(withId(R.id.frameLayout2)).check(matches(isDisplayed()))
    }

    @Test
    fun testCameraButtonIsClickable() {
        // Check if the camera capture button is clickable
        onView(withId(R.id.image_capture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.image_capture_button)).perform(click())
         }

    @Test
    fun testPermissions() {
        // Test camera permissions
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

        // Check if permission is denied
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            // Assuming permission is denied initially, request it

            // Launch the activity to simulate the permission request
            ActivityScenario.launch(MainActivity::class.java)

            // Increment IdlingResource to handle async behavior
            idlingResource.increment()

            // Wait for the permission request process to complete
            Espresso.registerIdlingResources(idlingResource)

            // Simulate the permission grant (or check if permission is granted now)
            val newPermissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

            assert(newPermissionStatus == PackageManager.PERMISSION_GRANTED)

            // Decrement the IdlingResource
            idlingResource.decrement()
        }
    }
    }
