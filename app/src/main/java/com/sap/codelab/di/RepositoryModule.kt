package com.sap.codelab.di

import com.sap.codelab.domain.IMemoRepository
import org.koin.dsl.module
import com.sap.codelab.data.repository.Repository

val repositoryModule = module {
    single<IMemoRepository> { Repository(get()) }
}