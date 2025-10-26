package com.sap.codelab.presentation.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.presentation.home.diffutils.MemoDiffCallback
import com.sap.codelab.presentation.model.MemoUI

/**
 * Adapter containing a set of memos.
 */
internal class MemoAdapter(
    private val onMemoClick: (Long) -> Unit,
    private val onDoneClick: (Long, Boolean) -> Unit
) : ListAdapter<MemoUI, MemoViewHolder>(MemoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewTypee: Int): MemoViewHolder {
        val binding = RecyclerviewMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoViewHolder(binding, onMemoClick, onDoneClick)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
