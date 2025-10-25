package com.sap.codelab.domain.usecases

import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo

class UpdateMemoDoneStatusUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(memo: Memo, isChecked: Boolean) {
        if (isChecked) {
            val updatedMemo = memo.copy(isDone = true)
            repository.saveMemo(updatedMemo)
        }
    }
}
