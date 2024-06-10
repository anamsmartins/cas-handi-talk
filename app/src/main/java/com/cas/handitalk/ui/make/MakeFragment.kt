package com.cas.handitalk.ui.make

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cas.handitalk.databinding.FragmentMakeBinding
import com.cas.handitalk.ui.guess.MakeViewModel

class MakeFragment : Fragment() {

    private var _binding: FragmentMakeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val makeViewModel =
            ViewModelProvider(this).get(MakeViewModel::class.java)

        _binding = FragmentMakeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMake
        makeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}