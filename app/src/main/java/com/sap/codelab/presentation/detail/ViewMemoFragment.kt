package com.sap.codelab.presentation.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.sap.codelab.databinding.FragmentViewMemoBinding
import com.sap.codelab.presentation.base.BaseFragment
import com.sap.codelab.presentation.model.MemoUI
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewMemoFragment : BaseFragment<FragmentViewMemoBinding>(FragmentViewMemoBinding::inflate) {

    private val viewModel: ViewMemoViewModel by viewModel()
    private val args: ViewMemoFragmentArgs by navArgs()

    override val toolbar: Toolbar?
        get() = binding.toolbar

    override fun FragmentViewMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()

        if (savedInstanceState == null) {
            viewModel.loadMemo(args.memoId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.memoUiModel.collect { memoUi ->
                        memoUi?.let { updateUI(it) }
                    }
                }

                launch {
                    viewModel.errorState.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            viewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(memoUi: MemoUI) {
        binding.contentCreateMemo.run {
            memoTitle.setText(memoUi.title)
            memoDescription.setText(memoUi.description)
            memoTitle.isEnabled = false
            memoDescription.isEnabled = false
        }
    }
}
