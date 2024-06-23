package com.google.mediapipe.examples.gesturerecognizer.ui.quick_learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.mediapipe.examples.gesturerecognizer.R
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

        // Button CAS
        val buttonCAS: Button = root.findViewById(R.id.buttonQLCAS)
        buttonCAS.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "CAS")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button Afraid
        val buttonAfraid: Button = root.findViewById(R.id.buttonQLAfraid)
        buttonAfraid.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "Afraid")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button A
        val buttonA: Button = root.findViewById(R.id.buttonQLA)
        buttonA.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "A")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button C
        val buttonC: Button = root.findViewById(R.id.buttonQLC)
        buttonC.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "C")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button S
        val buttonS: Button = root.findViewById(R.id.buttonQLS)
        buttonS.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "S")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button More
        val buttonMore: Button = root.findViewById(R.id.buttonQLMore)
        buttonMore.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "More")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button Sad
        val buttonSad: Button = root.findViewById(R.id.buttonQLSad)
        buttonSad.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "Sad")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        // Button Stop
        val buttonStop: Button = root.findViewById(R.id.buttonQLStop)
        buttonStop.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("quickLearnWord", "Stop")
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            ).navigate(R.id.action_quick_learn_to_make, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}