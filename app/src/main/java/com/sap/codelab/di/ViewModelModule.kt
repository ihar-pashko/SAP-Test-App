package com.sap.codelab.di

import com.sap.codelab.presentation.view.create.CreateMemoViewModel
import com.sap.codelab.presentation.view.detail.ViewMemoViewModel
import com.sap.codelab.presentation.view.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            getAllMemos = get(),
            getOpenMemos = get(),
            updateMemoDoneStatus = get()
        )
    }

    viewModel { ViewMemoViewModel(getMemoById = get()) }

    viewModel {
        CreateMemoViewModel(
            saveMemo = get(),
            validateMemo = get()
        )
    }
}
