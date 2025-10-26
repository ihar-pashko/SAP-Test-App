package com.sap.codelab.di

import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.data.mapper.MemoMapperImpl
import com.sap.codelab.presentation.MemoUIMapper
import com.sap.codelab.presentation.MemoUIMapperImpl
import org.koin.dsl.module

val domainMapperModule = module {
    single<MemoMapper> { MemoMapperImpl() }
}

val uiMapperModule = module {
    single<MemoUIMapper> { MemoUIMapperImpl() }
}
