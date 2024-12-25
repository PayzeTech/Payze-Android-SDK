package com.payze.sdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayzeActivityData(
    val language: Language,
    val transactionId: String,
    val companyLogoRes: Int = -1,
    val amount: Money,
    val environment: ServiceEnvironment,
    val isLogEnabled: Boolean
): Parcelable {

    companion object {
        const val KEY_DATA = "payze_data_key"
    }
}
