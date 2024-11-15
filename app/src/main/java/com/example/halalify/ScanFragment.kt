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
import java.util.*

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private var imageCapture: ImageCapture? = null
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS
            )
        }

        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        return binding.root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    analyzeBarcode(photoFile)
                }
            }
        )
    }

    private fun analyzeBarcode(file: File) {
        val inputImage = InputImage.fromFilePath(requireContext(), Uri.fromFile(file))
        val scanner = BarcodeScanning.getClient()

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_PRODUCT -> {
                            val rawValue = barcode.rawValue
                            Log.d(TAG, "Barcode detected: $rawValue")
                            Toast.makeText(requireContext(), "Product Barcode: $rawValue", Toast.LENGTH_SHORT).show()
                            saveBarcodeToFirestore(rawValue)
                        }
                        else -> {
                            Log.d(TAG, "Other barcode type detected")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Barcode detection failed: ${e.message}", e)
                Toast.makeText(requireContext(), "No barcode found", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveBarcodeToFirestore(barcodeValue: String?) {
        val data = hashMapOf(
            "barcode" to barcodeValue,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("barcodes").add(data)
            .addOnSuccessListener {
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "ScanFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
