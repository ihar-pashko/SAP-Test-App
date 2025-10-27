package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class AddGeofenceForMemoUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(memo: Memo): Result<Unit> {
        return repository.addGeofenceForMemo(memo, GEOFENCE_RADIUS_METERS)
    }

    companion object {
        private const val GEOFENCE_RADIUS_METERS = 200f
    }
}
