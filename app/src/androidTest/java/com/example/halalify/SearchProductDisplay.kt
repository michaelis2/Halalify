import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.halalify.R
import com.example.halalify.searchproductresult
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchProductDisplay {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference
    private lateinit var mockProductData: Map<String, Any>
    private lateinit var randomProductId: String

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<searchproductresult>(
        Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            searchproductresult::class.java
        ).apply {
            putExtra("product_name", "Test Product")
            putExtra("product_image", "https://example.com/image.jpg")
            putExtra("calories", "150")
            putExtra("category", "Snacks")
            putExtra("halal_status", "Halal")
        }
    )

    @Before
    fun setUp() {
        // Mock Firestore
        firestore = mockk(relaxed = true)
        collectionReference = mockk(relaxed = true)
        documentReference = mockk(relaxed = true)

        // Generate a random product ID (Mock Firebase random ID)
        randomProductId = "00mcrOntYTsKnABnBzOB"

        // Mock the product data
        mockProductData = mapOf(
            "name" to "Test Product",
            "image" to "https://example.com/image.jpg",
            "calories" to "150",
            "category" to "Snacks",
            "halal_status" to "Halal"
        )

        // Set up Firestore behavior
        every { firestore.collection("products") } returns collectionReference
        every { collectionReference.document(randomProductId) } returns documentReference
        every { documentReference.get() } returns Tasks.forResult(mockk {
            every { exists() } returns true
            every { data } returns mockProductData
        })
    }

    @Test
    fun testUIElements() {
        // Execute the Firestore query to get product data
        val task = firestore.collection("products").document(randomProductId).get()

        // Verify Firestore query was called
        verify { collectionReference.document(randomProductId) }

        // Assert that the document exists and data matches
        assert(task.isSuccessful)
        assert(task.result?.exists() == true)
        assert(task.result?.data == mockProductData)

        // Verify UI elements in the Activity
        onView(withId(R.id.textView14)).check(matches(withText("Test Product")))
        onView(withId(R.id.textView18)).check(matches(withText("Category: Snacks")))
        onView(withId(R.id.textView19)).check(matches(withText("Halal Status: Halal")))
        onView(withId(R.id.calorydisplay)).check(matches(withText("Calorie: 150")))
        onView(withId(R.id.imageoffood)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        unmockkAll() // Unmock all objects
    }
}
