package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class GetAllMemosUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(): List<Memo> {
        return repository.getAll()
    }
}
