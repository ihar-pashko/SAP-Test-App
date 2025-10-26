package com.sap.codelab.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.sap.codelab.databinding.FragmentViewMemoBinding
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.viewmodels.ViewMemoViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewMemoFragment : BaseFragment<FragmentViewMemoBinding>(FragmentViewMemoBinding::inflate) {

    private val viewModel: ViewMemoViewModel by viewModel()
    private val args: ViewMemoFragmentArgs by navArgs()

    override val toolbar: Toolbar?
        get() = binding.toolbar

    override fun FragmentViewMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.memoModel.collect { value ->
                    value?.let { memo ->
                        updateUI(memo)
                    }
                }
            }

            viewModel.loadMemo(args.memoId)
        }
    }

    private fun updateUI(memo: Memo) {
        binding.contentCreateMemo.run {
            memoTitle.setText(memo.title)
            memoDescription.setText(memo.description)
            memoTitle.isEnabled = false
            memoDescription.isEnabled = false
        }
    }
}
