package com.sap.codelab.data

import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.Memo

class MemoMapperImpl : MemoMapper {

    override fun fromModelToUI(model: MemoModel): Memo {
        return Memo(
            id = model.id,
            title = model.title,
            description = model.description,
            reminderDate = model.reminderDate,
            reminderLatitude = model.reminderLatitude,
            reminderLongitude = model.reminderLongitude,
            isDone = model.isDone
        )
    }
}