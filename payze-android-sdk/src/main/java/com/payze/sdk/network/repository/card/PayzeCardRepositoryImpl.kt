package com.payze.sdk.network.repository.card

import com.payze.sdk.network.ResultWrapper
import com.payze.sdk.network.model.PayzeCardBrandResponse
import com.payze.sdk.network.model.PayzePayRequest
import com.payze.sdk.network.model.PayzePayResponse
import com.payze.sdk.network.safeApiCall
import com.payze.sdk.network.service.PayzeCardService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class PayzeCardRepositoryImpl(
    val cardService: PayzeCardService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PayzeCardRepository {

    override suspend fun getCardBrand(cardNumber: String): ResultWrapper<Response<PayzeCardBrandResponse>> {
        return safeApiCall(dispatcher) {
            cardService.getCardBrand(cardNumber)
        }
    }

    override suspend fun pay(request: PayzePayRequest): ResultWrapper<Response<PayzePayResponse>> {
        return safeApiCall(dispatcher) {
            cardService.pay(request)
        }
    }
}