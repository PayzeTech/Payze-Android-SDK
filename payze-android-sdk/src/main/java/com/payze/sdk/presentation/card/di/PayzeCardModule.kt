package com.payze.sdk.presentation.card.di

import com.payze.sdk.network.repository.card.PayzeCardRepository
import com.payze.sdk.network.repository.card.PayzeCardRepositoryImpl
import com.payze.sdk.presentation.card.vm.PayzeCardVm
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cardModule = module {
    viewModel {
        PayzeCardVm(cardRepository = get())
    }

    single<PayzeCardRepository> {
        PayzeCardRepositoryImpl(
            cardService = get()
        )
    }
}