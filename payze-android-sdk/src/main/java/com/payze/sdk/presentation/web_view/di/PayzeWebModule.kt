package com.payze.sdk.presentation.web_view.di

import com.payze.sdk.presentation.web_view.vm.PayzeWebVm
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val webModule = module {
    viewModel {
        PayzeWebVm()
    }
}