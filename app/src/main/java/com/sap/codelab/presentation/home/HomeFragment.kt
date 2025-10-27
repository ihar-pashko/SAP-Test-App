package com.sap.codelab.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentHomeBinding
import com.sap.codelab.presentation.base.BaseFragment
import com.sap.codelab.presentation.create.CreateMemoFragment
import com.sap.codelab.presentation.home.adapters.MemoAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModel()

    override val toolbar: Toolbar
        get() = binding.toolbar

    override fun FragmentHomeBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        val memoAdapter = initializeAdapter()
        setupRecyclerView(memoAdapter)

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createMemoFragment)
        }

        setFragmentResultListener(CreateMemoFragment.REQUEST_KEY_MEMO_CREATED) { _, _ ->
            viewModel.onRefreshMemos()
        }

        setupCustomToolbarActions()
        observeFilterState()
        observeErrorState()
        viewModel.onShowAllMemosSelected()
    }

    private fun initializeAdapter(): MemoAdapter {
        val adapter = MemoAdapter(
            onMemoClick = { memoId ->
                val action = HomeFragmentDirections.actionHomeFragmentToViewMemoFragment(memoId)
                findNavController().navigate(action)
            },
            onDoneClick = { memoId, isChecked ->
                viewModel.onMemoDoneStatusChanged(memoId, isChecked)
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.memos.collect { uiMemos ->
                    adapter.submitList(uiMemos)
                }
            }
        }
        return adapter
    }

    private fun setupRecyclerView(adapter: MemoAdapter) {
        binding.contentHome.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            this.adapter = adapter
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
            )
        }
    }

    private fun setupCustomToolbarActions() {
        binding.actionShowAll.setOnClickListener {
            viewModel.onShowAllMemosSelected()
        }
        binding.actionShowOpen.setOnClickListener {
            viewModel.onShowOpenMemosSelected()
        }
    }

    private fun observeFilterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentFilter.collect { filterType ->
                    when (filterType) {
                        MemoFilterType.ALL -> {
                            binding.actionShowOpen.isVisible = true
                            binding.actionShowAll.isVisible = false
                        }

                        MemoFilterType.OPEN -> {
                            binding.actionShowAll.isVisible = true
                            binding.actionShowOpen.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun observeErrorState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorState.collect { errorMessage ->
                    errorMessage?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }
}
