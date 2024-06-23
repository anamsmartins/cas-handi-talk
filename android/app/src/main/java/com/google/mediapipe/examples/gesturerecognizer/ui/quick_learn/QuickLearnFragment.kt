package com.google.mediapipe.examples.gesturerecognizer.ui.quick_learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentQuickLearnBinding

class QuickLearnFragment : Fragment() {

    private var _binding: FragmentQuickLearnBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val quickLearnViewModel =
            ViewModelProvider(this).get(QuickLearnViewModel::class.java)

        _binding = FragmentQuickLearnBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textQuickLearn
//        quickLearnViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}