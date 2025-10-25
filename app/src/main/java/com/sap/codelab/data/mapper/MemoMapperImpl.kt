package com.sap.codelab.data.mapper

import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.model.Memo

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

    override fun fromUIToModel(memo: Memo): MemoModel {
        return MemoModel(
            id = memo.id,
            title = memo.title,
            description = memo.description,
            reminderDate = memo.reminderDate,
            reminderLatitude = memo.reminderLatitude,
            reminderLongitude = memo.reminderLongitude,
            isDone = memo.isDone
        )
    }
}
