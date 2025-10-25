package com.sap.codelab.presentation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.databinding.ActivityViewMemoBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

internal const val BUNDLE_MEMO_ID: String = "memoId"

/**
 * Activity that allows a user to see the details of a memo.
 */
internal class ViewMemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMemoBinding
    private val model: ViewMemoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // Initialize views with the passed memo id
        if (savedInstanceState == null) {
            // Observe the memo state flow for changes
            lifecycleScope.launch {
                model.memoModel.collect { value ->
                    value?.let { memo ->
                        // Update the UI whenever the memo changes
                        updateUI(memo)
                    }
                }
            }
            val id = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
            model.loadMemo(id)
        }
    }

    /**
     * Updates the UI with the given memo details.
     *
     * @param memoModel - the memo whose details are to be displayed.
     */
    private fun updateUI(memoModel: MemoModel) {
        binding.contentCreateMemo.run {
            memoTitle.setText(memoModel.title)
            memoDescription.setText(memoModel.description)
            memoTitle.isEnabled = false
            memoDescription.isEnabled = false
        }
    }
}
