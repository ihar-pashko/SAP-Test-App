package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class GetOpenMemosUseCase(private val repository: MemoRepository) {

    suspend operator fun invoke(): Result<List<Memo>> {
        return repository.getOpen()
    }
}
