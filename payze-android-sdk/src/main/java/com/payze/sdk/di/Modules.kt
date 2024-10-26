package com.payze.sdk.di

import com.payze.sdk.BuildConfig
import com.payze.sdk.model.ServiceEnvironment
import com.payze.sdk.network.service.PayzeCardService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun networkModule(env: ServiceEnvironment?) = module {
    single { createOkHttpClient() }

    single { createRetrofit(okHttpClient = get(), env = env) }

    single { provideService(retrofit = get()) }
}

private fun createOkHttpClient() = OkHttpClient.Builder().build()

private fun createRetrofit(okHttpClient: OkHttpClient, env: ServiceEnvironment?) =
    Retrofit.Builder()
        .baseUrl(
            when(env) {
                ServiceEnvironment.DEVELOPMENT -> BuildConfig.BASE_URL_DEV
                ServiceEnvironment.PRODUCTION -> BuildConfig.BASE_URL_PROD
                else -> BuildConfig.BASE_URL_DEV
            }
        )
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

private fun provideService(retrofit: Retrofit): PayzeCardService =
    retrofit.create(PayzeCardService::class.java)