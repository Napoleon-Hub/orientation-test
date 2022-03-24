package com.funnygaytest.ui.fragments.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.funnygaytest.BuildConfig
import com.funnygaytest.R
import com.funnygaytest.databinding.FragmentStartBinding
import com.funnygaytest.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.funnygaytest.utils.extentions.setOnClickListener

@AndroidEntryPoint
class StartFragment : BaseFragment() {

    private lateinit var binding: FragmentStartBinding
    private val viewModel: StartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fillQuestionsDatabase(requireContext())
    }

    override fun initUI() = binding.run {
        btnStart.setOnClickListener(500) {
            if (!viewModel.isConnected) {
                showErrorDialog(
                    R.string.dialog_connection_error_title,
                    R.string.dialog_connection_error_description,
                    R.string.dialog_connection_error_button
                )
                return@setOnClickListener
            }
            if (!viewModel.gameBegun) viewModel.gameBegun = true
            navigate(StartFragmentDirections.actionStartFragmentToGameFragment())
        }
        if (viewModel.gameBegun) {
            btnStart.text = getText(R.string.start_button_continue)
            tvStartDescription.text = getText(R.string.start_description_continue)
        } else {
            btnStart.text = getText(R.string.start_button)
            tvStartDescription.text = getText(R.string.start_description)
        }
        tvStartVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
    }

}