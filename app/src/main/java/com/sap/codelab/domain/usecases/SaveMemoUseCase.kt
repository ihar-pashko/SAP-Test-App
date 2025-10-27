package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class SaveMemoUseCase(private val repository: MemoRepository) {

    suspend operator fun invoke(
        title: String,
        description: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Long> {
        val newMemo = Memo(
            id = 0,
            title = title,
            description = description,
            reminderLatitude = latitude,
            reminderLongitude = longitude,
            isDone = false
        )
        return repository.saveMemo(newMemo)
    }
}
