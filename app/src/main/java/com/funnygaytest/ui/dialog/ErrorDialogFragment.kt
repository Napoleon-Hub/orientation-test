package com.funnygaytest.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.funnygaytest.R
import com.funnygaytest.databinding.DialogErrorBinding
import com.funnygaytest.utils.extentions.setOnClickListener

class ErrorDialogFragment(
    private val textTitle: String,
    private val textDescription: String,
    private val textButton: String
) : DialogFragment() {

    private lateinit var binding: DialogErrorBinding

    override fun getTheme() = R.style.DialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogErrorBinding.bind(inflater.inflate(R.layout.dialog_error, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() = binding.run {
        tvDialogTitle.text = textTitle
        tvDialogDescription.text = textDescription
        btnDialog.apply {
            text = textButton
            setOnClickListener(500) { dialog?.dismiss() }
        }
    }
}