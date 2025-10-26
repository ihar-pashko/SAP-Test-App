package com.sap.codelab.di

import com.sap.codelab.presentation.viewmodels.CreateMemoViewModel
import com.sap.codelab.presentation.viewmodels.HomeViewModel
import com.sap.codelab.presentation.viewmodels.ViewMemoViewModel
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
