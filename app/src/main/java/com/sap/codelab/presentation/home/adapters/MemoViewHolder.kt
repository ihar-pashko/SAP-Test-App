package com.sap.codelab.presentation.home.adapters

import androidx.recyclerview.widget.RecyclerView
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.presentation.model.MemoUI

/**
 * View holder for Memos.
 */
internal class MemoViewHolder(
    private val binding: RecyclerviewMemoBinding,
    private val onMemoClick: (Long) -> Unit,
    private val onDoneClick: (Long, Boolean) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var currentMemoUi: MemoUI? = null

    init {
        itemView.setOnClickListener {
            currentMemoUi?.let { uiModel ->
                onMemoClick(uiModel.id)
            }
        }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentMemoUi?.let { uiModel ->
                if (binding.checkBox.isEnabled && uiModel.isDone != isChecked) {
                    onDoneClick(uiModel.id, isChecked)
                }
            }
        }
    }

    fun bind(memoUi: MemoUI) {
        currentMemoUi = memoUi

        binding.run {
            memoTitle.text = memoUi.title
            memoText.text = memoUi.description

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = memoUi.isDone
            checkBox.isEnabled = !memoUi.isDone
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                currentMemoUi?.let { current ->
                    if (binding.checkBox.isEnabled && current.isDone != isChecked) {
                        onDoneClick(current.id, isChecked)
                    }
                }
            }
        }
    }
}
