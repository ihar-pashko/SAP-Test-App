package com.sap.codelab.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.usecases.GetAllMemosUseCase
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.domain.usecases.GetOpenMemosUseCase
import com.sap.codelab.domain.usecases.UpdateMemoDoneStatusUseCase
import com.sap.codelab.presentation.mapper.MemoUIMapper
import com.sap.codelab.presentation.model.MemoUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MemoFilterType {
    ALL,
    OPEN
}

/**
 * ViewModel for the Home Activity.
 */
internal class HomeViewModel(
    private val getAllMemosUseCase: GetAllMemosUseCase,
    private val getOpenMemosUseCase: GetOpenMemosUseCase,
    private val updateMemoDoneStatusUseCase: UpdateMemoDoneStatusUseCase,
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val memoUIMapper: MemoUIMapper
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(MemoFilterType.OPEN)
    val currentFilter: StateFlow<MemoFilterType> = _currentFilter.asStateFlow()

    private val _memos: MutableStateFlow<List<MemoUI>> = MutableStateFlow(listOf())
    val memos: StateFlow<List<MemoUI>> = _memos.asStateFlow()

    private val _errorState: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    init {
        loadMemos()
    }

    fun onShowAllMemosSelected() {
        _currentFilter.update { MemoFilterType.ALL }
        loadMemos()
    }

    fun onShowOpenMemosSelected() {
        _currentFilter.update { MemoFilterType.OPEN }
        loadMemos()
    }

    fun onRefreshMemos() {
        loadMemos()
    }

    fun onMemoDoneStatusChanged(memoId: Long, isChecked: Boolean) {
        if (isChecked) {
            viewModelScope.launch {
                getMemoByIdUseCase(memoId)
                    .onSuccess { domainMemo ->
                        updateMemoDoneStatusUseCase(domainMemo, true)
                            .onSuccess {
                                loadMemos()
                            }
                            .onFailure { updateError ->
                                Log.e("HomeViewModel", "Error updating memo status", updateError)
                                _errorState.update { "Could not update memo status." }
                            }
                    }
                    .onFailure { getError ->
                        Log.e("HomeViewModel", "Error getting memo by ID for update", getError)
                        _errorState.update { "Could not load memo for update." }
                    }
            }
        }
    }

    private fun loadMemos() {
        viewModelScope.launch {
            val result = when (_currentFilter.value) {
                MemoFilterType.ALL -> getAllMemosUseCase()
                MemoFilterType.OPEN -> getOpenMemosUseCase()
            }

            result.onSuccess { domainMemos ->
                _memos.value = memoUIMapper.fromDomainListToUI(domainMemos)
                _errorState.update { null }
            }.onFailure { error ->
                Log.e("HomeViewModel", "Error loading memos", error)
                _errorState.update { "List of memos could not be loaded." }
                _memos.value = emptyList()
            }
        }
    }

    fun onClearError() {
        _errorState.update { null }
    }
}
