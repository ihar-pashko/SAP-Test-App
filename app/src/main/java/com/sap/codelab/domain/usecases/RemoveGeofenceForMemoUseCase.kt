package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository

class RemoveGeofenceForMemoUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(memoId: Long): Result<Unit> {
        return repository.removeGeofenceForMemo(memoId)
    }
}
