package com.sap.codelab.data.mapper

import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.model.Memo

interface MemoMapper {

    fun fromModelToUI(model: MemoModel): Memo
    fun fromUIToModel(memo: Memo): MemoModel
}
