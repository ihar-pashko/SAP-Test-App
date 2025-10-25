package com.sap.codelab.di

import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.data.mapper.MemoMapperImpl
import org.koin.dsl.module

val mapperModule = module {
    single<MemoMapper> { MemoMapperImpl() }
}
