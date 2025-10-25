package com.sap.codelab.di

import com.sap.codelab.data.repository.MemoRepositoryImpl
import com.sap.codelab.domain.MemoRepository
import org.koin.dsl.module

val memoRepositoryImplModule = module {
    single<MemoRepository> { MemoRepositoryImpl(get()) }
}
