package com.sap.codelab.presentation.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentCreateMemoBinding
import com.sap.codelab.presentation.viewmodels.CreateMemoViewModel
import com.sap.codelab.utils.extensions.empty
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateMemoFragment :
    BaseFragment<FragmentCreateMemoBinding>(FragmentCreateMemoBinding::inflate) {

    private val viewModel: CreateMemoViewModel by viewModel()

    companion object {
        const val REQUEST_KEY_MEMO_CREATED = "memo_created_request"
    }

    override val toolbar: Toolbar?
        get() = binding.toolbar

    override fun FragmentCreateMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.menu_create_memo, menu)
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.action_save -> {
                            saveMemo()
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun saveMemo() {
        binding.contentCreateMemo.run {
            viewModel.updateMemo(memoTitle.text.toString(), memoDescription.text.toString())
            if (viewModel.isMemoValid()) {
                viewModel.saveMemo()
                setFragmentResult(REQUEST_KEY_MEMO_CREATED, bundleOf())
                findNavController().popBackStack()
            } else {
                memoTitleContainer.error =
                    getErrorMessage(viewModel.hasTitleError(), R.string.memo_title_empty_error)
                memoDescription.error =
                    getErrorMessage(viewModel.hasTextError(), R.string.memo_text_empty_error)
            }
        }
    }

    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String {
        return if (hasError) {
            requireContext().getString(errorMessageResId)
        } else {
            String.Companion.empty()
        }
    }
}
