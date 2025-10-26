package com.sap.codelab.presentation.create

data class CreateMemoValidationState(
    val hasTitleError: Boolean = false,
    val hasDescriptionError: Boolean = false
)
