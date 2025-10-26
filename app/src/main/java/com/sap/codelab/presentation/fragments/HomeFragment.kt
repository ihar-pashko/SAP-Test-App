package com.sap.codelab.presentation.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentHomeBinding
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.adapters.MemoAdapter
import com.sap.codelab.presentation.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModel()
    private var menuItemShowAll: MenuItem? = null
    private var menuItemShowOpen: MenuItem? = null

    override val toolbar: Toolbar?
        get() = binding.toolbar

    override fun FragmentHomeBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        val memoAdapter = initializeAdapter()
        setupVerticalRecyclerView(contentHome.recyclerView, memoAdapter)

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createMemoFragment)
        }

        setFragmentResultListener(CreateMemoFragment.REQUEST_KEY_MEMO_CREATED) { _, _ ->
            viewModel.refreshMemos()
        }

        setupMenu()
        viewModel.loadAllMemos()
    }

    private fun initializeAdapter(): MemoAdapter {
        val adapter = MemoAdapter(mutableListOf(), { view ->
            val memo = view.tag as Memo
            val action = HomeFragmentDirections.actionHomeFragmentToViewMemoFragment(memo.id)
            findNavController().navigate(action)
        }, { checkbox, isChecked ->
            val memo = checkbox.tag as Memo
            viewModel.updateMemo(memo, isChecked)
            viewModel.refreshMemos()
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memos.collect { memos ->
                adapter.setItems(memos)
            }
        }
        return adapter
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_home, menu)
                    menuItemShowAll = menu.findItem(R.id.action_show_all)
                    menuItemShowOpen = menu.findItem(R.id.action_show_open)
                    menuItemShowOpen?.isVisible = false
                    menuItemShowAll?.isVisible = true
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.action_show_all -> {
                            viewModel.loadAllMemos()
                            menuItemShowAll?.isVisible = false
                            menuItemShowOpen?.isVisible = true
                            true
                        }

                        R.id.action_show_open -> {
                            viewModel.loadOpenMemos()
                            menuItemShowOpen?.isVisible = false
                            menuItemShowAll?.isVisible = true
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
}
