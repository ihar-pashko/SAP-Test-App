package com.sap.codelab.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule = module {
    single<CoroutineDispatcher>(named(Dispatchers.IO::class.simpleName.orEmpty())) {
        Dispatchers.IO
    }

    single<CoroutineDispatcher>(named(Dispatchers.Main::class.simpleName.orEmpty())) {
        Dispatchers.Main
    }

    single<CoroutineDispatcher>(named(Dispatchers.Default::class.simpleName.orEmpty())) {
        Dispatchers.Default
    }
}
