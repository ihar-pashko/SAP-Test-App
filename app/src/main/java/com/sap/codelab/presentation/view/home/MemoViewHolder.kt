package com.sap.codelab.presentation.view.home

import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.databinding.RecyclerviewMemoBinding

/**
 * View holder for Memos.
 */
internal class MemoViewHolder(private val binding: RecyclerviewMemoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Updates the memo view with the given memo.
     */
    fun update(
        memoModel: MemoModel,
        onClick: View.OnClickListener,
        onCheckboxChanged: CompoundButton.OnCheckedChangeListener
    ) {
        binding.run {
            memoTitle.text = memoModel.title
            memoText.text = memoModel.description
        }
        updateCheckbox(memoModel, onCheckboxChanged)
        // This is needed if the user selects a given memo to show the detail screen
        itemView.tag = memoModel
        itemView.setOnClickListener(onClick)
    }

    /**
     * Updates the checkbox view.
     */
    private fun updateCheckbox(
        memoModel: MemoModel,
        onCheckboxChanged: CompoundButton.OnCheckedChangeListener
    ) {
        // if the view is reused it will already have a listener already set on it. So in order this not to be called when the value is initialized
        // we remove the listener and set it back.
        binding.checkBox.apply {
            setOnCheckedChangeListener(null)
            isChecked = memoModel.isDone
            // We only let the user edit the checkbox if the item has not been marked as "done"
            isEnabled = !memoModel.isDone
            // We need the memo if the user ticks the checkbox, so we can update the memo
            tag = memoModel
            setOnCheckedChangeListener(onCheckboxChanged)
        }
    }
}
