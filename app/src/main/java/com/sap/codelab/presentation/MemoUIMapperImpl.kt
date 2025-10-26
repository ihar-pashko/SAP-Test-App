package com.sap.codelab.presentation

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.model.MemoUI

class MemoUIMapperImpl : MemoUIMapper {

    override fun fromDomainListToUI(memos: List<Memo>): List<MemoUI> {
        return memos.map { fromDomainToUI(it) }
    }

    override fun fromDomainToUI(memo: Memo): MemoUI {
        return MemoUI(
            id = memo.id,
            title = memo.title,
            description = memo.description,
            isDone = memo.isDone
        )
    }
}
