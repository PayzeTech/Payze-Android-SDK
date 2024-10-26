package com.payze.sdk.network.model

data class PayzePayRequest(
    val transactionId: String,
    val number: String,
    val cardHolder: String,
    val expirationDate: String,
    val securityNumber: String
)