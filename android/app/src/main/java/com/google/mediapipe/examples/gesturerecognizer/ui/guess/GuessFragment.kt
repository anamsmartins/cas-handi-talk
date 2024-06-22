package com.google.mediapipe.examples.gesturerecognizer.ui.guess

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentGuessBinding

class GuessFragment : Fragment() {

    private var _binding: FragmentGuessBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val guessViewModel =
            ViewModelProvider(this).get(GuessViewModel::class.java)

        _binding = FragmentGuessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup video player
        videoView = root.findViewById(R.id.guess_video)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)

//        val videoUri: Uri = Uri.parse("android.resource://"+ requireContext().packageName + "/" +R.raw.samplevideo)
//        videoView.setVideoURI(videoUri)

//        videoView.setOnPreparedListener { mp ->
//            mp.setVolume(0f, 0f) // Remove volume
//            videoView.start()
//        }

        // Text button fields
        val textBtn = root.findViewById<Button>(R.id.textBtn)
        val textAnswerGuess = root.findViewById<EditText>(R.id.textAnswerGuess)
        val confirmTextAnswerGuessBtn = root.findViewById<Button>(R.id.confirmTextAnswerGuessBtn)

        // Voice button fields
        val voiceBtn = root.findViewById<Button>(R.id.voiceBtn)
        val voiceAnswerGuess = root.findViewById<TextView>(R.id.voiceAnswerGuess)

        // Setup text button listener
        textBtn.setOnClickListener {
            // Toggle visibility of the text button fields
            if (textAnswerGuess.visibility == EditText.VISIBLE) {
                textAnswerGuess.visibility = EditText.GONE
                confirmTextAnswerGuessBtn.visibility = Button.GONE
            } else {
                // Check voice button fields visibility
                if (voiceAnswerGuess.visibility == TextView.VISIBLE) {
                    voiceAnswerGuess.visibility = TextView.GONE
                }

                textAnswerGuess.visibility = EditText.VISIBLE
                confirmTextAnswerGuessBtn.visibility = Button.VISIBLE
            }
        }

        // Setup voice button listener
        voiceBtn.setOnClickListener {
            // Toggle visibility of the voice button fields
            if (voiceAnswerGuess.visibility == EditText.VISIBLE) {
                voiceAnswerGuess.visibility = EditText.GONE
            } else {
                // Check text button fields visibility
                if (textAnswerGuess.visibility == EditText.VISIBLE) {
                    textAnswerGuess.visibility = EditText.GONE
                    confirmTextAnswerGuessBtn.visibility = Button.GONE
                }

                voiceAnswerGuess.visibility = EditText.VISIBLE
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}