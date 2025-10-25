package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class GetMemoByIdUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(id: Long): Memo {
        return repository.getMemoById(id)
    }
}
