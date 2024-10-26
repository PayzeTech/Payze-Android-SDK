package com.payze.sdk.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.payze.sdk.model.PayzeActivityData
import com.payze.sdk.model.Currency
import com.payze.sdk.model.Language
import com.payze.sdk.model.Money
import com.payze.sdk.model.PayzeResult
import com.payze.sdk.model.ServiceEnvironment
import com.payze.sdk.presentation.PayzeActivity

class Payze(private val context: Context) {

    private val activity get() = context as AppCompatActivity

    private lateinit var getResult: ActivityResultLauncher<Intent>

    private var onResult: ((data: PayzeResult) -> Unit)? = null

    fun init() {
        getResult = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getIntExtra(RETURN_DATA, PayzeResult.IN_PROGRESS.value)
                onResult?.invoke(PayzeResult.fromValue(id = value))
            }
        }
    }

    fun start(
        language: Language?,
        transactionId: String,
        companyLogoRes: Int = -1,
        amount: Money,
        environment: ServiceEnvironment,
        onResult: ((data: PayzeResult) -> Unit)? = null
    ) {
        this.onResult = onResult

        val intent = Intent(context, PayzeActivity::class.java)
        intent.putExtra(
            PayzeActivityData.KEY_DATA,
            PayzeActivityData(
                language = language ?: Language.EN,
                transactionId = transactionId,
                companyLogoRes = companyLogoRes,
                amount = amount,
                environment = environment
            )
        )
        getResult.launch(intent)
    }

    companion object {
        const val RETURN_DATA = "return_data"
    }
}