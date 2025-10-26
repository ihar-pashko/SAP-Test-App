package com.sap.codelab.presentation

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.model.MemoUI

interface MemoUIMapper {
    fun fromDomainToUI(memo: Memo): MemoUI
    fun fromDomainListToUI(memos: List<Memo>): List<MemoUI>
}
