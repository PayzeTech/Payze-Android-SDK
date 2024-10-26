package com.payze.sdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Language(val prefix: String): Parcelable {
    EN("en"),
    RU("ru"),
    UZ("uz"),
}