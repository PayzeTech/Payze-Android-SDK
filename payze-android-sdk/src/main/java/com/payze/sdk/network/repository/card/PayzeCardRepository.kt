package com.payze.sdk.network.repository.card

import com.payze.sdk.network.ResultWrapper
import com.payze.sdk.network.model.PayzeCardBrandResponse
import com.payze.sdk.network.model.PayzePayRequest
import com.payze.sdk.network.model.PayzePayResponse
import retrofit2.Response

interface PayzeCardRepository {
    suspend fun getCardBrand(cardNumber: String): ResultWrapper<Response<PayzeCardBrandResponse>>

    suspend fun pay(request: PayzePayRequest): ResultWrapper<Response<PayzePayResponse>>
}