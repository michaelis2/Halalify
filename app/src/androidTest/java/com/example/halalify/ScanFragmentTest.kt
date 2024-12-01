/*package com.example.halalify

import android.Manifest
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.test.core.app.ActivityScenario
import com.example.halalify.ScanFragment
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import java.util.concurrent.CompletableFuture

class ScanFragmentTest {

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    // Mock Firestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    @Before
    fun setUp() {
        // Initialize mocks
        MockKAnnotations.init(this)

        // Mock Firestore and CollectionReference
        firestore = mockk()
        collection = mockk()

        // Mock the behavior of Firestore collection
        every { firestore.collection("barcodes") } returns collection
        every { collection.add(any()) } returns mockk()
    }



    @Test
    fun testCameraInitializationAndPhotoCapture() {ActivityScenario.launch(MainActivity::class.java)
        val scenario = launchFragmentInContainer<ScanFragment>()
        scenario.onFragment { fragment ->
            // Verify that the camera preview is displayed
            onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
            scenario.moveToState(Lifecycle.State.RESUMED)

            // Click the capture button
            onView(withId(R.id.image_capture_button)).perform(click())

            // Verify that an image capture is triggered
            val mockImageCapture: ImageCapture = mockk(relaxed = true)
            val mockFile =
                Uri.parse("/Users/amandairawan/StudioProjects/halalify/app/src/main/res/drawable/halalify.png")

            every {
                mockImageCapture.takePicture(any(), any(), any<ImageCapture.OnImageSavedCallback>())
            } answers {
                thirdArg<ImageCapture.OnImageSavedCallback>().onImageSaved(
                    ImageCapture.OutputFileResults(mockFile)
                )
            }
            verify { mockImageCapture.takePicture(any(), any(), any()) }

        }
    }

    @Test
    fun testBarcodeDetectionAndFirestoreSaving() {
        // Launch the ScanFragment
        val scenario = launchFragmentInContainer<ScanFragment>()

        scenario.onFragment { fragment ->
            // Mock Firestore behavior inside the fragment
            val barcodeValue = "1234567890"
            val data = hashMapOf("barcode" to barcodeValue)

            every {
                firestore.collection("barcodes").add(data)
            } returns mockk()

            // Simulate a button click to trigger barcode saving
            onView(withId(R.id.image_capture_button)).perform(click())

            // Verify that the barcode value is saved to Firestore
            verify { firestore.collection("barcodes").add(any()) }
        }
    }
    @After
    fun tearDown() {
        unmockkAll() // Unmock all objects and static methods
    }
}
*/