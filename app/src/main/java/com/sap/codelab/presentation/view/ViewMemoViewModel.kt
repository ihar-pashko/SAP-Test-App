package com.sap.codelab.presentation.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.presentation.MemoUIMapper
import com.sap.codelab.presentation.model.MemoUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for matching ViewMemo view.
 */
internal class ViewMemoViewModel(
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val uiMapper: MemoUIMapper
) : ViewModel() {

    private val _memoUiModel: MutableStateFlow<MemoUI?> = MutableStateFlow(null)
    val memoUiModel: StateFlow<MemoUI?> = _memoUiModel.asStateFlow()

    private val _errorState: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    /**
     * Loads the memo whose id matches the given memoId from the database.
     */
    fun loadMemo(memoId: Long) {
        viewModelScope.launch {
            getMemoByIdUseCase(memoId)
                .onSuccess { domainMemo ->
                    _memoUiModel.update { uiMapper.fromDomainToUI(domainMemo) }
                    _errorState.update { null }
                }
                .onFailure { error ->
                    Log.e("ViewMemoViewModel", "Error loading memo $memoId", error)
                    _errorState.update { "Cound not load memo." }
                    _memoUiModel.update { null }
                }
        }
    }

    fun clearError() {
        _errorState.update { null }
    }
}
