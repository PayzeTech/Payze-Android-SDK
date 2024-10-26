package com.payze.sdk.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Money(
    val amount: Double,
    val currency: Currency
): Parcelable