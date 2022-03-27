package com.funnygaytest.ui.fragments.result

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.funnygaytest.R
import com.funnygaytest.databinding.FragmentResultBinding
import com.funnygaytest.ui.base.BaseFragment
import com.funnygaytest.utils.MAX_POINTS
import com.funnygaytest.utils.extentions.disable
import com.funnygaytest.utils.extentions.setOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BaseFragment() {

    private lateinit var binding: FragmentResultBinding
    private val viewModel: ResultViewModel by viewModels()

    private lateinit var backPressedCallback: OnBackPressedCallback
    private var resultText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initBilling()
        viewModel.refreshGameInfo()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback = requireActivity()
            .onBackPressedDispatcher
            .addCallback { activity?.finish() }
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.remove()
    }

    override fun initUI() {
        setupButtons()
        setupTextResult()
    }

    private fun initObservers() {
        viewModel.skuDetails.observe(viewLifecycleOwner) { scuDetails ->
            if (scuDetails?.price != null) {
                binding.tvResultNotice.text = getString(R.string.result_text_full, scuDetails.price)
            } else {
                binding.tvResultNotice.text = getString(R.string.result_text_notice)
                binding.btnPay.disable()
            }
        }
    }

    private fun setupButtons() = binding.run {
        btnRestart.setOnClickListener(500) {
            if (!viewModel.isConnected) {
                showErrorDialog(
                    R.string.dialog_connection_error_title,
                    R.string.dialog_connection_error_description,
                    R.string.dialog_connection_error_button
                )
                return@setOnClickListener
            }
            navigateBack()
            viewModel.gameBegun = true
        }
        btnShare.setOnClickListener(500) { share() }
        btnPay.setOnClickListener(500) { viewModel.launchBillingFlow(requireActivity()) }
        btnRateUs.setOnClickListener(500) { rateUs() }
    }

    private fun setupTextResult() {
        val result = ((viewModel.points.toDouble() / MAX_POINTS) * 100).toInt()
        resultText = when {
            result < 25 -> getString(R.string.result_text_result_not_gay, result)
            result < 50 -> getString(R.string.result_text_result_little_gay, result)
            result < 75 -> getString(R.string.result_text_result_probably_gay, result)
            else -> getString(R.string.result_text_result_definitely_gay, result)
        }
        binding.tvResultDescription.text = resultText
    }

    private fun share() {
        val shareText = getString(R.string.result_test_share, resultText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = SHARE_TEXT_TYPE
        }

        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun rateUs() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI)))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URI)))
        }
    }

    companion object {
        private const val SHARE_TEXT_TYPE = "text/plain"
        private const val MARKET_URI = "market://details?id=com.funnygaytest"
        private const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=com.funnygaytest"
    }

}