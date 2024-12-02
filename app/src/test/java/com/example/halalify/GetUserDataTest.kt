import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetUserDataTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference
    private lateinit var mockUserData: Map<String, Any>

    private lateinit var randomUserId: String

    @Before
    fun setUp() {
        // Mock Firestore
        firestore = mockk(relaxed = true)
        collectionReference = mockk(relaxed = true)
        documentReference = mockk(relaxed = true)

        // Generate a random user ID (Firebase-generated ID, e.g. "0g0p3IEEAng5vBpAeARsramMdwE3")
        randomUserId = "0g0p3IEEAng5vBpAeARsramMdwE3"  // Mock Firebase random ID

        // Mock the user data
        mockUserData = mapOf(
            "age" to 97,
            "height" to 97,
            "username" to "nij",
            "weight" to 97
        )

        // Set up Firestore behavior
        every { firestore.collection("users") } returns collectionReference
        every { collectionReference.document(randomUserId) } returns documentReference
        every { documentReference.get() } returns Tasks.forResult(mockk {
            every { exists() } returns true
            every { data } returns mockUserData
        })
    }

    @Test
    fun `test user data exists in Firestore`() {
        // Use the random userId in the test
        val userId = randomUserId

        // Execute the Firestore query to check if user data exists
        val task = firestore.collection("users").document(userId).get()

        // Verify Firestore query was called with the correct parameters
        verify { collectionReference.document(userId) }

        // Assert that the document exists and the data matches
        assert(task.isSuccessful)
        assert(task.result?.exists() == true)
        assert(task.result?.data == mockUserData)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
