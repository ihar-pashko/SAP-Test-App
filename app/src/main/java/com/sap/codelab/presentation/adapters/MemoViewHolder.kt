package com.sap.codelab.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.domain.model.Memo

/**
 * View holder for Memos.
 */
internal class MemoViewHolder(
    private val binding: RecyclerviewMemoBinding,
    private val onMemoClick: (Memo) -> Unit,
    private val onDoneClick: (Memo, Boolean) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var currentMemo: Memo? = null

    init {
        itemView.setOnClickListener {
            currentMemo?.let { memo ->
                onMemoClick(memo)
            }
        }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentMemo?.let { memo ->
                if (binding.checkBox.isEnabled && memo.isDone != isChecked) {
                    onDoneClick(memo, isChecked)
                }
            }
        }
    }

    fun bind(memo: Memo) {
        currentMemo = memo

        binding.run {
            memoTitle.text = memo.title
            memoText.text = memo.description

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = memo.isDone
            checkBox.isEnabled = !memo.isDone
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                currentMemo?.let { current ->
                    if (binding.checkBox.isEnabled && current.isDone != isChecked) {
                        onDoneClick(current, isChecked)
                    }
                }
            }
        }
    }
}
