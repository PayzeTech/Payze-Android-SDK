package com.payze.sdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ServiceEnvironment: Parcelable {
    DEVELOPMENT,
    PRODUCTION
}