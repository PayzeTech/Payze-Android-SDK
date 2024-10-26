package com.payze.sdk.network.model

data class PayzePayResponse(
    val success: Boolean,
    val url: String? = null,
    val threeDSIsPresent: Boolean
)
