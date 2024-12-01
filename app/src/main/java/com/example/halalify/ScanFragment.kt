package com.example.halalify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.halalify.databinding.FragmentScanBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ScanFragment : Fragment() {

    // Initialize variable
    private lateinit var binding: FragmentScanBinding
    private var imageCapture: ImageCapture? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)

        if (allPermissionsGranted()) {
            startCamera() // Start the camera if permissions are granted
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS
            )
        }
        // Take photo when the button is clicked
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        return binding.root
    }


    // Start the camera and bind it to the lifecycle
    private fun startCamera() {

        // Get the camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        // When the camera provider is ready, set up the camera use cases
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview for the camera
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }


            // Set up image capture use case
            imageCapture = ImageCapture.Builder().build()

            // Select the back camera for the preview
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind any existing use cases and bind the new ones
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                // Handle errors during use case binding
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Function to take a photo using the camera
    private fun takePhoto() {

        // Check if imageCapture is initialized
        val imageCapture = imageCapture ?: return

        // Create a file where the captured photo will be saved
        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Set up output options to save the captured photo to the file
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                // Handle errors during photo capture
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                }

                // Successful image capture
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    analyzeBarcode(photoFile) // Analyze the barcode in the captured photo
                }
            }
        )
    }

    // Analyze the barcode in the captured photo
    private fun analyzeBarcode(file: File) {

        // Convert the photo file into an InputImage for barcode detection
        val inputImage = InputImage.fromFilePath(requireContext(), Uri.fromFile(file))
        val scanner = BarcodeScanning.getClient()

        // Process the image and detect barcodes
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                // If barcodes are found, process each barcode
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_PRODUCT -> {
                            val rawValue = barcode.rawValue
                            Log.d(TAG, "Barcode detected: $rawValue")
                            Toast.makeText(
                                requireContext(),
                                "Product Barcode: $rawValue",
                                Toast.LENGTH_SHORT
                            ).show()
                            saveBarcodeToFirestore(rawValue) // Save barcode to Firestore
                        }
                        else -> {
                            Log.d(TAG, "Other barcode type detected")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure during barcode detection
                Log.e(TAG, "Barcode detection failed: ${e.message}", e)
                Toast.makeText(requireContext(), "No barcode found", Toast.LENGTH_SHORT).show()
            }
    }


    // Function to save the barcode value to Firestore
    private fun saveBarcodeToFirestore(barcodeValue: String?) {

        // Compile barcode data
        val data = hashMapOf(
            "barcode" to barcodeValue,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("barcodes").add(data)
            .addOnSuccessListener {  // Show success message and pass barcode value to next activity
                Toast.makeText(requireContext(), "Barcode saved", Toast.LENGTH_SHORT).show()

                // Pass the barcode to scannedproductresult directly
                val intent = Intent(requireContext(), scannedproductresult::class.java)
                intent.putExtra("barcode", barcodeValue)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save barcode: ${e.message}", e)
            }
    }

    // Function to check if all required permissions (CAMERA) are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "ScanFragment" // Log tag for debugging
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS" // Date format for filenames
        private const val REQUEST_CODE_PERMISSIONS = 10 // Request code for permissions
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA) // List of required permissions
    }
}