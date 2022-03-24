package com.funnygaytest.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.funnygaytest.ui.dialog.ErrorDialogFragment

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    abstract fun initUI()

    fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    fun navigateBack() {
        findNavController().popBackStack()
    }

    fun toast(@StringRes messageStringRes: Int) {
        Toast.makeText(requireContext(), messageStringRes, Toast.LENGTH_SHORT).show()
    }

    fun showErrorDialog(titleId: Int, descriptionId: Int, buttonTextId: Int) {
        ErrorDialogFragment(
            textTitle = getString(titleId),
            textDescription = getString(descriptionId),
            textButton = getString(buttonTextId),
        ).show(childFragmentManager, null)
    }

}