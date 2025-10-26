package com.sap.codelab.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.diffutils.MemoDiffCallback

/**
 * Adapter containing a set of memos.
 */
internal class MemoAdapter(
    private val onMemoClick: (Memo) -> Unit,
    private val onDoneClick: (Memo, Boolean) -> Unit
) : ListAdapter<Memo, MemoViewHolder>(MemoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewTypee: Int): MemoViewHolder {
        val binding = RecyclerviewMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoViewHolder(binding, onMemoClick, onDoneClick)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = getItem(position)
        holder.bind(memo)
    }
}
