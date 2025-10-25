package com.sap.codelab.domain.usecases

class ValidateMemoUseCase {

    operator fun invoke(title: String, description: String): MemoValidationResult {
        val hasTitleError = title.isBlank()
        val hasTextError = description.isBlank()
        val isValid = !hasTitleError && !hasTextError

        return MemoValidationResult(
            isValid = isValid,
            hasTitleError = hasTitleError,
            hasTextError = hasTextError
        )
    }

    data class MemoValidationResult(
        val isValid: Boolean,
        val hasTitleError: Boolean,
        val hasTextError: Boolean
    )
}
