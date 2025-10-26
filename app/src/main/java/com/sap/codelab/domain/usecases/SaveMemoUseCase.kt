package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class SaveMemoUseCase(private val repository: MemoRepository) {

    suspend operator fun invoke(title: String, description: String) {
        val newMemo = Memo(
            id = 0,
            title = title,
            description = description,
            reminderDate = 0,
            reminderLatitude = 0,
            reminderLongitude = 0,
            isDone = false
        )
        repository.saveMemo(newMemo)
    }
}
