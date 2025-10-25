package com.sap.codelab.presentation.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.MemoRepository
import com.sap.codelab.utils.coroutines.ScopeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Activity.
 */
internal class HomeViewModel(private val repository: MemoRepository) : ViewModel() {

    private var isShowAll = false
    private val _memos: MutableStateFlow<List<MemoModel>> = MutableStateFlow(listOf())
    val memos: StateFlow<List<MemoModel>> = _memos

    /**
     * Loads all memos.
     */
    fun loadAllMemos() {
        isShowAll = true
        viewModelScope.launch(Dispatchers.Default) {
            _memos.value = repository.getAll()
        }
    }

    /**
     * Loads all open (not done) memos.
     */
    fun loadOpenMemos() {
        isShowAll = false
        viewModelScope.launch(Dispatchers.Default) {
            _memos.value = repository.getOpen()
        }
    }

    fun refreshMemos() {
        if (isShowAll) {
            loadAllMemos()
        } else {
            loadOpenMemos()
        }
    }

    /**
     * Updates the given memo, marking it as done if isChecked is true.
     *
     * @param memoModel      - the memo to update.
     * @param isChecked - whether the memo has been checked (marked as done).
     */
    fun updateMemo(memoModel: MemoModel, isChecked: Boolean) {
        ScopeProvider.application.launch(Dispatchers.Default) {
            // We'll only forward the update if the memo has been checked, since we don't offer to uncheck memos right now
            if (isChecked) {
                repository.saveMemo(memoModel.copy(isDone = true))
            }
        }
    }
}
