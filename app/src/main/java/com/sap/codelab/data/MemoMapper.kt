package com.sap.codelab.data

import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.Memo

interface MemoMapper {

    fun fromModelToUI(model: MemoModel): Memo
}
