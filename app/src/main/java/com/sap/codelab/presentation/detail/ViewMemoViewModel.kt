package com.sap.codelab.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.presentation.MemoUIMapper
import com.sap.codelab.presentation.model.MemoUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for matching ViewMemo view.
 */
internal class ViewMemoViewModel(
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val uiMapper: MemoUIMapper
) : ViewModel() {

    private val _memoId = MutableStateFlow<Long?>(null)

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val memoDomainFlow: StateFlow<Memo?> = _memoId
        .filterNotNull()
        // UseCase возвращает Result<Memo>
        .mapLatest { id -> getMemoByIdUseCase(id) }
        .map { result ->
            result.fold(
                onSuccess = { memo -> memo },
                onFailure = { error ->
                    Log.e("ViewMemoViewModel", "Error loading memo ${_memoId.value}", error)
                    _errorState.value = "Не удалось загрузить заметку."
                    null
                }
            )
        }
        .catch { e ->
            Log.e("ViewMemoViewModel", "Exception in memoDomainFlow for ${_memoId.value}", e)
            _errorState.value = "Произошла ошибка при загрузке."
            emit(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val memoUiModel: StateFlow<MemoUI?> = memoDomainFlow
        .map { domainMemo -> domainMemo?.let { uiMapper.fromDomainToUI(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val locationLatLng: StateFlow<LatLng?> = memoDomainFlow
        .map { domainMemo ->
            domainMemo?.reminderLatitude?.let { lat ->
                domainMemo.reminderLongitude?.let { lon ->
                    LatLng(lat, lon)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setMemoId(memoId: Long) {
        if (_memoId.value != memoId) {
            _memoId.value = memoId
        }
    }

    fun onClearError() {
        _errorState.value = null
    }
}
