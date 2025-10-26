package com.sap.codelab.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.usecases.SaveMemoUseCase
import com.sap.codelab.domain.usecases.ValidateMemoUseCase
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
internal class CreateMemoViewModel(
    private val saveMemoUseCase: SaveMemoUseCase,
    private val validateMemoUseCase: ValidateMemoUseCase
) : ViewModel() {

    private val _title = MutableStateFlow(String.Companion.empty)
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow(String.Companion.empty)
    val description = _description.asStateFlow()
    private val _validationState = MutableStateFlow(CreateMemoValidationState())
    val validationState = _validationState.asStateFlow()

    private val _eventChannel = Channel<CreateMemoEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onActionSaveClicked() {
        val currentValidationResult = validateInternal()
        _validationState.update {
            CreateMemoValidationState(
                hasTitleError = currentValidationResult.hasTitleError,
                hasDescriptionError = currentValidationResult.hasTextError
            )
        }

        if (currentValidationResult.isValid) {
            viewModelScope.launch {
                saveMemoUseCase(
                    title = _title.value,
                    description = _description.value
                )
                _eventChannel.send(CreateMemoEvent.NavigateBack)
            }
        }
    }

    fun updateMemoInput(title: String, description: String) {
        var needsValidationReset = false
        _title.update {
            if (it != title) needsValidationReset = true
            title
        }
        _description.update {
            if (it != description) needsValidationReset = true
            description
        }
        if (needsValidationReset) {
            _validationState.update { CreateMemoValidationState() }
        }
    }

    private fun validateInternal(): ValidateMemoUseCase.MemoValidationResult =
        validateMemoUseCase(_title.value, _description.value)
}
