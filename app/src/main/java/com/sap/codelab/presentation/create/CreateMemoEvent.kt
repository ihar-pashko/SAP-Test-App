package com.sap.codelab.presentation.create

sealed interface CreateMemoEvent {
    data class NavigateBackWithSuccess(val memoId: Long) : CreateMemoEvent
    data class ShowError(val message: String) : CreateMemoEvent
}
