package com.sap.codelab.presentation.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.sap.codelab.domain.model.Memo

object MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {

    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem == newItem
    }
}
