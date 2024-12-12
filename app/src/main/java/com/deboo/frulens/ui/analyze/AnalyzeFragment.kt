package com.deboo.frulens.ui.analyze

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.deboo.frulens.R
import com.deboo.frulens.api.FruitClient.apiInstance
import com.deboo.frulens.data.Serving
import com.deboo.frulens.databinding.FragmentAnalyzeBinding
import com.deboo.frulens.ui.cam.CameraActivity
import com.deboo.frulens.utils.ImageClassifierHelper
import com.deboo.frulens.utils.reduceFileImage
import com.deboo.frulens.utils.rotateBitmap
import com.deboo.frulens.utils.saveBitmapToCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class AnalyzeFragment : Fragment() {

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE = 100
    }

    private val viewModel: AnalyzeViewModel by viewModels()
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var token: String
    private var file: File? = null

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val launcherCameraXIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val theFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) ?: true
            file = reduceFileImage(theFile)
            val rotatedBitmap = rotateBitmap(
                BitmapFactory.decodeFile(theFile.path),
                isBackCamera
            )
            // Convert the Bitmap to a Uri
            val uri = saveBitmapToCache(rotatedBitmap, requireContext())
            viewModel.currentImageUri = uri

            showImage(viewModel.currentImageUri)
        }
    }

    private val launcherGalleryIntent =  registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            showImage(viewModel.currentImageUri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun showImage(uri: Uri?) {
        uri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }
    private fun showResults(results: String?) {
        results?.let {
            binding.resAnalyze.text = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val analyzeViewModel = ViewModelProvider(this)[AnalyzeViewModel::class.java]

        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textResult
        if (analyzeViewModel.servedata != ""){
            binding.textResult.text = getString(R.string.result)
        }else{
            analyzeViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }
        }
        analyzeViewModel.currentImageUri?.let { uri ->
            showImage(uri)
        }
        analyzeViewModel.servedata.let { results ->
            showResults(results)
            binding.textResult.text = getString(R.string.result)
        }

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = this
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUEST_PERMISSIONS,
                REQUEST_CODE
            )
        }
    }

    private fun setupListener() {
        binding.cameraButton.setOnClickListener {
            openCamera()
        }
        binding.galleryButton.setOnClickListener {
            openGallery()
        }
        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun analyzeImage() {
        viewModel.currentImageUri?.let { uri ->
            // Show the progress bar
            binding.progressIndicator.visibility = View.VISIBLE

            // Launch a coroutine to perform classification
            lifecycleScope.launch {
                imageClassifierHelper.classifyStaticImage(uri)
            }
        } ?: run {
            showToast("Please select an image first.")
        }
    }


    private fun openCamera() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherCameraXIntent.launch(intent)
    }

    private fun openGallery() {
        launcherGalleryIntent.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        showLoading(false)
        results?.let {
            var concLabel = ""
            var highscore = 0f
            val allResults = mutableListOf<String>()

            // Iterate over all classifications and categories to find the highest score
            it.forEach { classification ->
                classification.categories.forEach { category ->
                    allResults.add("Label: ${category.label}, Score: ${category.score}")
                    if (category.score > highscore) {
                        highscore = category.score * 100
                        concLabel = category.label
                    }
                }
            }

            viewModel.identification = ("Conclusion: Identified $concLabel $highscore%" +
                    "\n" +
                    "Inference Time: $inferenceTime ms")

            fetchNutritionData(concLabel)
        }
    }

    private fun fetchNutritionData(fruit: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Show the loading indicator
                showLoading(true)

                // Fetch data in the IO context
                val response = withContext(Dispatchers.IO) {
                    apiInstance.getNutrition(fruit)
                }

                // Hide the loading indicator
                showLoading(false)

                // Get the first serving as the primary serving
                val primaryServing = response.servings.firstOrNull()
                val otherServings = response.servings.drop(1) // Store other servings

                primaryServing?.let { serving ->
                    viewModel.servedata = """
                    Food: ${response.food_name}
                    Serving: ${serving.serving_description}
                    Calories: ${serving.calories} kcal
                    Carbohydrates: ${serving.carbohydrate} g
                    Protein: ${serving.protein} g
                    Fat: ${serving.fat} g
                    Fiber: ${serving.fiber} g
                    Sugar: ${serving.sugar} g
                    Vitamin C: ${serving.vitamin_c} mg
                    Calcium: ${serving.calcium} mg
                """.trimIndent()

                    showResults(viewModel.servedata)
                    binding.textResult.text = getString(R.string.result)
                } ?: run {
                    binding.resAnalyze.text = getString(R.string.no_serving)
                }

                // Process and store other servings for future use
                if (otherServings.isNotEmpty()) {
                    viewModel.otherservedata = otherServings
                    processOtherServings(otherServings)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showLoading(false) // Hide loading in case of error
                binding.textResult.text = getString(R.string.failfetch)
            }
        }
    }


    private fun processOtherServings(otherServings: List<Serving>) {
        // Logic to handle or store other servings, e.g., save to a database or cache
        otherServings.forEach { serving ->
            Log.d("OtherServing", "Serving: ${serving.serving_description}, Calories: ${serving.calories}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (!allPermissionsGranted()) {
                Toast.makeText(requireContext(), getString(R.string.notallowed), Toast.LENGTH_LONG)
                    .show()
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUEST_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun onError(error: String) {
        showToast(error)
    }

    private fun showLoading(state: Boolean) {
        binding.progressIndicator.visibility = if (state) View.VISIBLE else View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
