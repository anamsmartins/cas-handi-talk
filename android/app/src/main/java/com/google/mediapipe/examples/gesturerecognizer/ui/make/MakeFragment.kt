package com.google.mediapipe.examples.gesturerecognizer.ui.make

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.mediapipe.examples.gesturerecognizer.GestureRecognizerHelper
import com.google.mediapipe.examples.gesturerecognizer.LearningWord
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentMakeBinding
import com.google.mediapipe.examples.gesturerecognizer.fragment.GestureRecognizerResultsAdapter
import com.google.mediapipe.examples.gesturerecognizer.fragment.PermissionsFragment
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MakeFragment : Fragment(), GestureRecognizerHelper.GestureRecognizerListener {

    companion object {
        private const val TAG = "Hand gesture recognizer"
    }

    private var _binding: FragmentMakeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var makeViewModel: MakeViewModel

    private var defaultNumResults = 1
    private val gestureRecognizerResultAdapter: GestureRecognizerResultsAdapter by lazy {
        GestureRecognizerResultsAdapter().apply {
            updateAdapterSize(defaultNumResults)
        }
    }
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: androidx.camera.core.Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    private lateinit var guessingWord: String
    private var won: Boolean = false

    private var isCASWord: Boolean = false
    private lateinit var wordC: TextView
    private lateinit var wordA: TextView
    private lateinit var wordS: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        makeViewModel =
            ViewModelProvider(this).get(MakeViewModel::class.java)

        _binding = FragmentMakeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val quickLearnWord = arguments?.getString("quickLearnWord")

        val textViewGoalMake = binding.root.findViewById(R.id.textGuessingWordMake) as TextView
        if (quickLearnWord == null) {
            // Generate random guessing word
            guessingWord = generateGuessingWord().toString()
        } else {
            guessingWord = quickLearnWord
        }

        textViewGoalMake.text = guessingWord

        if (guessingWord == "CAS"){
            isCASWord = true;

            wordC = binding.root.findViewById(R.id.textCMake) as TextView
            wordA = binding.root.findViewById(R.id.textAMake) as TextView
            wordS = binding.root.findViewById(R.id.textSMake) as TextView

            wordC.text = "_"
            wordA.text = "_"
            wordS.text = "_"
        }

        // Button Preview
        val buttonPreview: Button = root.findViewById(R.id.buttonMakePreview)
        buttonPreview.setOnClickListener {
            showPreviewImagePopup(guessingWord + ".png")
        }

        return root
    }

    private fun showPreviewImagePopup(imageName: String) {
        // Create a dialog with custom layout
        val dialog = Dialog(binding.root.context)
        dialog.setContentView(R.layout.preview_image)

        // Find ImageView in the custom layout
        val imageView = dialog.findViewById<ImageView>(R.id.image_preview_imageview)

        // Load image from assets
        try {
            // Assuming your images are in the "assets" folder
            // Access asset manager from the context
            val assetManager = context?.assets
            val inputStream = assetManager?.open(imageName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Optional: Set dialog size or other properties if needed
        dialog.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.image_preview_width),
            resources.getDimensionPixelSize(R.dimen.image_preview_height)
        )

        // Show the dialog
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(
                requireActivity(), R.id.fragment_container
            ).navigate(R.id.action_camera_to_permissions)
        }

        // Start the GestureRecognizerHelper again when users come back
        // to the foreground.
        backgroundExecutor.execute {
            if (gestureRecognizerHelper.isClosed()) {
                gestureRecognizerHelper.setupGestureRecognizer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::gestureRecognizerHelper.isInitialized) {
            makeViewModel.setMinHandDetectionConfidence(gestureRecognizerHelper.minHandDetectionConfidence)
            makeViewModel.setMinHandTrackingConfidence(gestureRecognizerHelper.minHandTrackingConfidence)
            makeViewModel.setMinHandPresenceConfidence(gestureRecognizerHelper.minHandPresenceConfidence)
            makeViewModel.setDelegate(gestureRecognizerHelper.currentDelegate)

            // Close the Gesture Recognizer helper and release resources
            backgroundExecutor.execute { gestureRecognizerHelper.clearGestureRecognizer() }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        with(binding.recyclerviewResults) {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = gestureRecognizerResultAdapter
//        }

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Wait for the views to be properly laid out
        binding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }

        // Create the Hand Gesture Recognition Helper that will handle the
        // inference
        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = makeViewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = makeViewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = makeViewModel.currentMinHandPresenceConfidence,
                currentDelegate = makeViewModel.currentDelegate,
                gestureRecognizerListener = this
            )
        }

    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        recognizeHand(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(MakeFragment.TAG, "Use case binding failed", exc)
        }
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper.recognizeLiveStream(
            imageProxy = imageProxy,
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            binding.viewFinder.display.rotation
    }

    // Update UI after a hand gesture has been recognized. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView. Only one result is expected at a time. If two or more
    // hands are seen in the camera frame, only one will be processed.
    override fun onResults(
        resultBundle: GestureRecognizerHelper.ResultBundle
    ) {
        activity?.runOnUiThread {
            if (_binding != null) {
                // Show result of recognized gesture
                val gestureCategories = resultBundle.results.first().gestures()
                val resultCategoryTextView = binding.root.findViewById(R.id.textResultCategoryMake) as TextView
                val resultAccuracyTextView = binding.root.findViewById(R.id.textResultAccuracyMake) as TextView

                if (gestureCategories.isNotEmpty()) {
                    val resultTest = gestureCategories.first().first()
                    val resultCategoryString = "Result: <b> ${resultTest.categoryName()}</b>"
                    resultCategoryTextView.setText(Html.fromHtml(resultCategoryString))
                    val resultScoreString = "Accuracy: ${resultTest.score()}"
                    resultAccuracyTextView.text = resultScoreString
//                    resultTextView.text = "Result: " + resultTest.categoryName() + "; Score: " + resultTest.score()

                    gestureRecognizerResultAdapter.updateResults(
                        gestureCategories.first()
                    )

                    checkIfWon(resultTest.categoryName())

                } else {
                    resultCategoryTextView.text = "--"
                    resultAccuracyTextView.text = ""
                    gestureRecognizerResultAdapter.updateResults(emptyList())
                }
//
//                binding.bottomSheetLayout.inferenceTimeVal.text =
//                    String.format("%d ms", resultBundle.inferenceTime)



                // Pass necessary information to OverlayView for drawing on the canvas
                binding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                binding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            gestureRecognizerResultAdapter.updateResults(emptyList())
        }
    }

    fun generateGuessingWord(): LearningWord {
        val learningWords: Array<LearningWord> = LearningWord.values()
        val randomIndex: Int = Random().nextInt(learningWords.size)
        return learningWords[randomIndex]
    }

    fun checkIfWon(category: String){
        if (!isCASWord) {
            if (category == guessingWord && !won) {
                won = true
                guessedWord()
            }
        }else{
            if (category == "C"){
                if (wordC.text !== category){
                    wordC.text = category
                }
            } else if (category == "A"){
                if (wordA.text !== category){
                    wordA.text = category
                }
            } else if (category == "S"){
                if (wordS.text !== category){
                    wordS.text = category
                }
            }
            if ((wordC.text == "C" && wordA.text == "A" && wordS.text == "S") && !won){
                won = true
                guessedWord()
            }

        }
    }

    fun guessedWord(){
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("Guessed the word!")
            builder.setMessage("Congratulations on guessing the word! 👊")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment_content_main
                ).navigate(R.id.action_make_to_home)
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
    }

    fun endModelRecognition(){
        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        endModelRecognition()
    }
}