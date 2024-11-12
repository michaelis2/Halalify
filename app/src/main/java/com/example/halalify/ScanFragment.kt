package com.example.halalify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.halalify.databinding.FragmentScanBinding
import androidx.camera.core.Preview.SurfaceProvider

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanBinding.inflate(inflater, container, false)

        // Initialize the camera feed
        startCamera()

        return binding.root

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Get the camera provider
            val cameraProvider = cameraProviderFuture.get()

            // Create the preview use case
            val preview = Preview.Builder()
                .build()

            // Bind the preview use case to the PreviewView
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            // Choose the camera selector (front or back camera)
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK) // You can change to LENS_FACING_FRONT for front camera
                .build()

            try {
                // Unbind any use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the camera use cases
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, // LifecycleOwner
                    cameraSelector,      // CameraSelector
                    preview              // Preview use case
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Camera initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {}
}
