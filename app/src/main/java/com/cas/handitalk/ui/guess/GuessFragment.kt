package com.cas.handitalk.ui.guess

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cas.handitalk.R
import com.cas.handitalk.databinding.FragmentGuessBinding

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

        videoView = root.findViewById(R.id.guess_video)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)

        val videoUri: Uri = Uri.parse("android.resource://"+ requireContext().packageName + "/" +R.raw.samplevideo)
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            mp.setVolume(0f, 0f) // Remove volume
            videoView.start()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}