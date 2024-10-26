package com.payze.sdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Currency(val value: String): Parcelable {
    USD("USD"),
    SUM("SUM")
}