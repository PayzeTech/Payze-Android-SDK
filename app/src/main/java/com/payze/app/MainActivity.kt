package com.payze.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.payze.app.databinding.ActivityMainBinding
import com.payze.sdk.manager.Payze
import com.payze.sdk.model.Currency
import com.payze.sdk.model.Language
import com.payze.sdk.model.Money
import com.payze.sdk.model.PayzeResult
import com.payze.sdk.model.ServiceEnvironment

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var payze: Payze
    private var language: Language? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        payze = Payze(context = this)

        payze.init()

        binding!!.mainLanguageBtn.setOnClickListener {
            when(binding!!.mainLanguageBtn.text) {
                Language.EN.prefix -> {
                    binding!!.mainLanguageBtn.text = Language.RU.prefix
                    language = Language.RU
                }
                Language.RU.prefix -> {
                    binding!!.mainLanguageBtn.text = Language.UZ.prefix
                    language = Language.UZ
                }
                Language.UZ.prefix -> {
                    binding!!.mainLanguageBtn.text = Language.EN.prefix
                    language = Language.EN
                }
            }
        }

        binding!!.mainBtn.setOnClickListener {
            payze.start(
                language = language,
                transactionId = binding!!.mainInput.getText() ?: "",
                companyLogoRes = R.drawable.ic_launcher_foreground,
                amount = Money(
                    amount = binding!!.mainAmountInput.getText()?.toDouble() ?: 0.0,
                    currency = if (binding!!.mainSUMCheck.isChecked) Currency.SUM else Currency.USD
                ),
                environment = if(binding!!.mainCheck.isChecked.not())
                    ServiceEnvironment.DEVELOPMENT
                else
                    ServiceEnvironment.PRODUCTION,
                onResult = {
                    when (it) {
                        PayzeResult.IN_PROGRESS -> showAlert(message = "In Progress")
                        PayzeResult.SUCCESS -> showAlert(message = "Success")
                        PayzeResult.FAIL -> showAlert(message = "Fail")
                    }
                }
            )
        }
    }

    private fun showAlert(message: String) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("Close") { _, _ -> }
            .show()
    }
}