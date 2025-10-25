package com.sap.codelab.presentation.view.create

import androidx.lifecycle.ViewModel
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.MemoRepository
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.launch

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
internal class CreateMemoViewModel(private val repository: MemoRepository) : ViewModel() {

    private var memoModel = MemoModel(0, String.empty(), String.empty(), 0, 0, 0, false)

    /**
     * Saves the memo in it's current state.
     */
    fun saveMemo() {
        ScopeProvider.application.launch {
            repository.saveMemo(memoModel)
        }
    }

    /**
     * Call this method to update the memo. This is usually needed when the user changed his input.
     */
    fun updateMemo(title: String, description: String) {
        memoModel = MemoModel(title = title, description = description, id = 0, reminderDate = 0, reminderLatitude = 0, reminderLongitude = 0, isDone = false)
    }

    /**
     * @return true if the title and content are not blank; false otherwise.
     */
    fun isMemoValid(): Boolean = memoModel.title.isNotBlank() && memoModel.description.isNotBlank()

    /**
     * @return true if the memo text is blank, false otherwise.
     */
    fun hasTextError() = memoModel.description.isBlank()

    /**
     * @return true if the memo title is blank, false otherwise.
     */
    fun hasTitleError() = memoModel.title.isBlank()
}