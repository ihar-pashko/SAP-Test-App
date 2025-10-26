package com.sap.codelab.di

import com.sap.codelab.data.repository.MemoRepositoryImpl
import com.sap.codelab.domain.interfaces.MemoRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<MemoRepository> {
        MemoRepositoryImpl(
            memoDao = get(),
            mapper = get(),
            ioDispatcher = get(named(Dispatchers.IO::class.simpleName.orEmpty()))
        )
    }
}
