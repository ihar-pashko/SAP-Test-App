package com.sap.codelab.di

import androidx.room.Room
import com.sap.codelab.data.database.AppDatabase
import com.sap.codelab.data.database.DbConstants.DATABASE_NAME
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
    single { get<AppDatabase>().getMemoDao() }
}