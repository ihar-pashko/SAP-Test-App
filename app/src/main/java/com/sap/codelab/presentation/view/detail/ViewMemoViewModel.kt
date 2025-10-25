package com.sap.codelab.presentation.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.MemoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for matching ViewMemo view.
 */
internal class ViewMemoViewModel(private val repository: MemoRepository) : ViewModel() {

    private val _memoModel: MutableStateFlow<MemoModel?> = MutableStateFlow(null)
    val memoModel: StateFlow<MemoModel?> = _memoModel

    /**
     * Loads the memo whose id matches the given memoId from the database.
     */
    fun loadMemo(memoId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            _memoModel.value = repository.getMemoById(memoId)
        }
    }
}