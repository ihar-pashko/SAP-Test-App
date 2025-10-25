package com.sap.codelab.presentation.view.create

import androidx.lifecycle.ViewModel
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.usecases.SaveMemoUseCase
import com.sap.codelab.domain.usecases.ValidateMemoUseCase
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.launch

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
internal class CreateMemoViewModel(
    private val saveMemo: SaveMemoUseCase,
    private val validateMemo: ValidateMemoUseCase
) : ViewModel() {

    private var memo = Memo(0, String.empty(), String.empty(), 0, 0, 0, false)
    private var validationResult: ValidateMemoUseCase.MemoValidationResult? = null

    /**
     * Saves the memo in it's current state.
     */
    fun saveMemo() {
        ScopeProvider.application.launch {
            saveMemo(memo)
        }
    }

    /**
     * Call this method to update the memo. This is usually needed when the user changed his input.
     */
    fun updateMemo(title: String, description: String) {
        memo = Memo(title = title, description = description, id = 0, reminderDate = 0, reminderLatitude = 0, reminderLongitude = 0, isDone = false)
        validationResult = null
    }

    /**
     * @return true if the title and content are not blank; false otherwise.
     */
    fun isMemoValid() = validate().isValid

    /**
     * @return true if the memo text is blank, false otherwise.
     */
    fun hasTextError() = validate().hasTextError

    /**
     * @return true if the memo title is blank, false otherwise.
     */
    fun hasTitleError() = validate().hasTitleError

    private fun validate(): ValidateMemoUseCase.MemoValidationResult {
        return validationResult ?: validateMemo(memo.title, memo.description).also {
            validationResult = it
        }
    }
}
