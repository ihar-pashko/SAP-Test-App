package com.sap.codelab.presentation.create

sealed class CreateMemoEvent {
    object NavigateBack : CreateMemoEvent()
}
