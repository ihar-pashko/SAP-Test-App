package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class SaveMemoUseCase(private val repository: MemoRepository) {

    suspend operator fun invoke(memo: Memo) {
        repository.saveMemo(memo)
    }
}
