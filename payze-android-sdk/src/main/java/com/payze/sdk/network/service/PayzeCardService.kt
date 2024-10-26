package com.payze.sdk.network.service

import com.payze.sdk.network.model.PayzeCardBrandResponse
import com.payze.sdk.network.model.PayzePayRequest
import com.payze.sdk.network.model.PayzePayResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PayzeCardService {

    @GET("v2/api/card/brand")
    suspend fun getCardBrand(@Query("bin") cardNumber: String): Response<PayzeCardBrandResponse>

    @POST("v2/payment/pay")
    suspend fun pay(@Body request: PayzePayRequest): Response<PayzePayResponse>
}