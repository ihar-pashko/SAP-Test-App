package com.sap.codelab.presentation.home.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.sap.codelab.presentation.model.MemoUI

object MemoDiffCallback : DiffUtil.ItemCallback<MemoUI>() {

    override fun areItemsTheSame(oldItem: MemoUI, newItem: MemoUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MemoUI, newItem: MemoUI): Boolean {
        return oldItem == newItem
    }
}
