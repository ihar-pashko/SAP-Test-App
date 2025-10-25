package com.sap.codelab.presentation.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.usecases.GetAllMemosUseCase
import com.sap.codelab.domain.usecases.GetOpenMemosUseCase
import com.sap.codelab.domain.usecases.UpdateMemoDoneStatusUseCase
import com.sap.codelab.utils.coroutines.ScopeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Activity.
 */
internal class HomeViewModel(
    private val getAllMemos: GetAllMemosUseCase,
    private val getOpenMemos: GetOpenMemosUseCase,
    private val updateMemoDoneStatus: UpdateMemoDoneStatusUseCase
) : ViewModel() {

    private var isShowAll = false
    private val _memos: MutableStateFlow<List<Memo>> = MutableStateFlow(listOf())
    val memos: StateFlow<List<Memo>> = _memos

    /**
     * Loads all memos.
     */
    fun loadAllMemos() {
        isShowAll = true
        viewModelScope.launch(Dispatchers.Default) {
            _memos.value = getAllMemos()
        }
    }

    /**
     * Loads all open (not done) memos.
     */
    fun loadOpenMemos() {
        isShowAll = false
        viewModelScope.launch(Dispatchers.Default) {
            _memos.value = getOpenMemos()
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
     * @param memo      - the memo to update.
     * @param isChecked - whether the memo has been checked (marked as done).
     */
    fun updateMemo(memo: Memo, isChecked: Boolean) {
        ScopeProvider.application.launch(Dispatchers.Default) {
            // We'll only forward the update if the memo has been checked, since we don't offer to uncheck memos right now
            if (isChecked) {
                updateMemoDoneStatus(memo, isChecked)
            }
        }
    }
}
