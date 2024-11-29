package com.payze.sdk.di

import com.payze.sdk.presentation.card.di.cardModule
import com.payze.sdk.presentation.web_view.di.webModule
import org.koin.dsl.koinApplication

internal object IsolatedKoinContext {
    val koinApp = koinApplication {
        modules(
            listOf(
                cardModule,
                webModule
            )
        )
    }

    val koin = koinApp.koin
}