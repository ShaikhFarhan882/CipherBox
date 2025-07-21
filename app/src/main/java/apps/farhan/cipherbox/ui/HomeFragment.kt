package apps.farhan.cipherbox.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import apps.farhan.cipherbox.R
import apps.farhan.cipherbox.databinding.FragmentHomeBinding
import apps.farhan.cipherbox.viewmodel.EncryptionViewModel


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EncryptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[EncryptionViewModel::class.java]

        setUpSpinner()

        binding.encryptButton.setOnClickListener {
            val text = binding.inputText.text.toString()
            val selectedMethod = binding.methodSpinner.selectedItem.toString()
            viewModel.encryptText(text, selectedMethod)
        }
        observeViewModel()

        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
    }

    private fun setUpSpinner(){
        // Initialize
        val methods = listOf("Base64", "MD5", "SHA-1", "SHA-256", "SHA-512")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            methods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.methodSpinner.adapter = adapter

        binding.methodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMethod = methods[position]
                Toast.makeText(requireContext(), "Selected: $selectedMethod", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun observeViewModel() {
        viewModel.encryptedText.observe(viewLifecycleOwner) { encryptedText ->
            binding.outputText.setText(encryptedText)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyToClipboard() {
        val textToCopy = binding.outputText.text.toString()

        if (textToCopy.isEmpty()) {
            Toast.makeText(requireContext(), "No text to copy", Toast.LENGTH_SHORT).show()
            return
        }

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("CipherBox Output", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
