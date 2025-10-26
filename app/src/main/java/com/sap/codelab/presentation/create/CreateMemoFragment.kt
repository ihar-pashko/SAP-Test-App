package com.sap.codelab.presentation.create

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentCreateMemoBinding
import com.sap.codelab.presentation.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateMemoFragment :
    BaseFragment<FragmentCreateMemoBinding>(FragmentCreateMemoBinding::inflate) {

    private val viewModel: CreateMemoViewModel by viewModel()

    override val toolbar: Toolbar?
        get() = binding.toolbar

    override fun FragmentCreateMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
        observeViewModel()
        setupInputListeners()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.validationState.collect { state ->
                        binding.contentCreateMemo.memoTitleContainer.error =
                            getErrorMessage(state.hasTitleError, R.string.memo_title_empty_error)
                        binding.contentCreateMemo.memoDescription.error =
                            getErrorMessage(
                                state.hasDescriptionError,
                                R.string.memo_text_empty_error
                            )
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is CreateMemoEvent.NavigateBack -> {
                                setFragmentResult(REQUEST_KEY_MEMO_CREATED, bundleOf())
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupInputListeners() {
        binding.contentCreateMemo.memoTitle.doAfterTextChanged { text ->
            viewModel.updateMemoInput(
                title = text.toString(),
                description = binding.contentCreateMemo.memoDescription.text.toString()
            )
        }
        binding.contentCreateMemo.memoDescription.doAfterTextChanged { text ->
            viewModel.updateMemoInput(
                title = binding.contentCreateMemo.memoTitle.text.toString(),
                description = text.toString()
            )
        }
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
                            viewModel.onActionSaveClicked()
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

    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String? {
        return if (hasError) {
            requireContext().getString(errorMessageResId)
        } else {
            null
        }
    }

    companion object {
        const val REQUEST_KEY_MEMO_CREATED = "memo_created_request"
    }
}
