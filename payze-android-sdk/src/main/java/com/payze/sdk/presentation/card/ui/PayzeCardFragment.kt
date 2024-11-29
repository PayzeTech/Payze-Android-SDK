package com.payze.sdk.presentation.card.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.payze.sdk.R
import com.payze.sdk.components.AMEX_MASK
import com.payze.sdk.components.DATE_KEYS
import com.payze.sdk.components.DATE_MASK
import com.payze.sdk.components.DEFAULT_MASK
import com.payze.sdk.components.NUMBER_KEYS
import com.payze.sdk.components.extensions.KeyboardHeightProvider
import com.payze.sdk.components.extensions.disableBtn
import com.payze.sdk.components.extensions.enableBtn
import com.payze.sdk.components.extensions.goAway
import com.payze.sdk.components.extensions.hideKeyboard
import com.payze.sdk.components.extensions.onSafeClick
import com.payze.sdk.components.extensions.show
import com.payze.sdk.components.extensions.validDate
import com.payze.sdk.components.formatter.AsteriskPasswordTransformationMethod
import com.payze.sdk.components.formatter.CustomTextWatcher
import com.payze.sdk.databinding.PayzeCardFragmentBinding
import com.payze.sdk.di.IsolatedKoinContext
import com.payze.sdk.manager.Payze.Companion.RETURN_DATA
import com.payze.sdk.model.CardBrandType
import com.payze.sdk.model.Language
import com.payze.sdk.model.Money
import com.payze.sdk.model.PayzeResult
import com.payze.sdk.presentation.PayzeActivity
import com.payze.sdk.presentation.card.vm.PayzeCardVm
import com.payze.sdk.presentation.web_view.ui.PayzeWebFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PayzeCardFragment : Fragment() {

    private var binding: PayzeCardFragmentBinding? = null
    private var companyLogoRes: Int = -1
    private var transactionId: String = ""
    private var language: Language? = null
    private var amount: Money? = null
    private val viewModel: PayzeCardVm = IsolatedKoinContext.koin.get()

    private val watcherAmex = CustomTextWatcher(AMEX_MASK)
    private val watcherDefault = CustomTextWatcher(DEFAULT_MASK)

    private var cardType: String = ""

    private var isAmex: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayzeCardFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArguments()
        initInputs()
        initObservers()

        binding!!.payzeCardCodeInput.input.transformationMethod = AsteriskPasswordTransformationMethod()
    }

    override fun onResume() {
        (activity as PayzeActivity).updateTitle(isCard = true)
        binding?.root?.post {
            if (isAdded)
                KeyboardHeightProvider(requireActivity(), this, ::onKeyboardHeightChange)
        }
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        with(viewModel) {
            brand.observe(viewLifecycleOwner) {
                updateBrand(brand = it?.brand)
            }

            brandError.observe(viewLifecycleOwner) {
                updateBrandLocally()
            }

            pay.observe(viewLifecycleOwner) {
                when {
                    it?.threeDSIsPresent == true && it.url.isNullOrEmpty().not() -> {
                        activity?.hideKeyboard()
                        parentFragmentManager.beginTransaction()
                            .add(
                                R.id.payzeMainContainer,
                                PayzeWebFragment.newInstance(
                                    url = it.url ?: return@observe
                                )
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                    it?.threeDSIsPresent == true -> {
                        (context as PayzeActivity).apply {
                            intent.putExtra(RETURN_DATA, PayzeResult.SUCCESS.value)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                    else -> {
                        showAlert(message = getString(R.string.payze_error_message))
                    }
                }
            }

            error.observe(viewLifecycleOwner) {
                (context as PayzeActivity).turnLoaderTransparent(show = false)
                showAlert(message = getString(R.string.payze_error_message))
            }
        }
    }

    private fun showCardIcon(iconRes: Int) {
        binding!!.payzeCardNumberInput.rightActionDrawableRes = iconRes
    }

    private fun initInputs() {
        with(binding!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                payzeCardNumberInput.input.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)
                payzeCardDateInput.input.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE)
                payzeCardCodeInput.input.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
            }

            updateCardFormat()

            payzeCardDateInput.input.addTextChangedListener(CustomTextWatcher(DATE_MASK))
            payzeCardDateInput.input.keyListener = DigitsKeyListener.getInstance(DATE_KEYS)

            payzeCardNumberInput.setListener {
                validateInfo()
                if (it.length >= 16) {
                    viewModel.getCardBrand(it.replace(" ", ""))
                }

                if (it.length < 16) {
                    payzeCardNumberInput.isDrawableVisible = false
                    payzeCardCodeInput.clearSetText("")
                    cardType = ""
                }
            }

            payzeCardDateInput.setListener {
                validateInfo()
            }

            payzeCardNameInput.setListener {
                validateInfo()
            }

            payzeCardCodeInput.setListener {
                validateInfo()
            }

            payzeCardActionBtn.onSafeClick {
                payzeCardNumberInput.getText()?.let { _ ->
                    if (validateInfo().not())
                        return@onSafeClick

                    (context as PayzeActivity).turnLoaderTransparent(show = true)

                    viewModel.pay(
                        transactionId = transactionId,
                        cardHolder = payzeCardNameInput.getText()!!,
                        number = payzeCardNumberInput.getText()?.replace(" ", "")!!,
                        secNumber = payzeCardCodeInput.getText()!!,
                        expDate = payzeCardDateInput.getText()?.replace(" ", "")!!
                    )
                }
            }

            payzeCardNameInput.input.filters += InputFilter.AllCaps()
        }
    }

    private fun validateInfo(): Boolean {
        with(binding!!) {

            val isNumberValid = payzeCardNumberInput.getText().isNullOrEmpty().not() &&
                    ((isAmex && payzeCardNumberInput.getText()?.length == 17) || (isAmex.not() && payzeCardNumberInput.getText()?.length == 19)) &&
                    isValidLuhn(payzeCardNumberInput.getText()?.replace(" ", "") ?: "")

            val isCodeValid = ((isAmex && payzeCardCodeInput.getText()?.length == 4) ||
                    (isAmex.not() && payzeCardCodeInput.getText()?.length == 3))

            val isDateValid = payzeCardDateInput.getText()?.length == 7 &&
                    payzeCardDateInput.getText()?.validDate() == true

            val isNameValid = payzeCardNameInput.getText().isNullOrEmpty().not()

            payzeCardDateInput.isError = isDateValid.not()
            payzeCardDateInput.isDefaultMode = isDateValid

            payzeCardCodeInput.isError = isCodeValid.not()
            payzeCardCodeInput.isDefaultMode = isCodeValid

            payzeCardNumberInput.isError = isNumberValid.not()
            payzeCardNumberInput.isDefaultMode = isNumberValid

            payzeCardNameInput.isError = isNameValid.not()
            payzeCardNameInput.isDefaultMode = isNameValid

            if (
                isNumberValid &&
                isDateValid &&
                isNameValid
            ) {
                if (payzeCardCodeInput.isVisible) {
                    if (isCodeValid) {
                        payzeCardActionBtn.enableBtn()
                        return true
                    } else {
                        payzeCardActionBtn.disableBtn()
                        return false
                    }
                } else {
                    payzeCardActionBtn.enableBtn()
                    return true
                }
            } else {
                payzeCardActionBtn.disableBtn()
                return false
            }
        }
    }

    private fun initArguments() {
        companyLogoRes = arguments?.getInt(COMPANY_LOGO) ?: -1
        transactionId = arguments?.getString(TRANSACTION_ID) ?: ""
        amount = arguments?.getParcelable(AMOUNT)
        language = arguments?.getParcelable(LANGUAGE)

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language?.prefix))

        binding?.payzeCardAmountValue?.text = "${amount?.amount} ${amount?.currency?.value}"

        binding?.let {
            if (companyLogoRes != -1) {
                it.payzeCardCompanyLogo.setImageResource(companyLogoRes)
            } else {
                it.payzeCardAmountTitle.gravity = Gravity.CENTER
                it.payzeCardAmountValue.gravity = Gravity.CENTER
            }
        }
    }

    private fun updateCardFormat(isAmex: Boolean = false) {
        this.isAmex = isAmex
        with(binding!!) {
            if (isAmex) {
                payzeCardNumberInput.input.removeTextChangedListener(watcherAmex)
                payzeCardNumberInput.input.removeTextChangedListener(watcherDefault)
                payzeCardNumberInput.input.addTextChangedListener(watcherAmex)
                payzeCardNumberInput.input.keyListener = DigitsKeyListener.getInstance(NUMBER_KEYS)
            } else {
                payzeCardNumberInput.input.removeTextChangedListener(watcherAmex)
                payzeCardNumberInput.input.removeTextChangedListener(watcherDefault)
                payzeCardNumberInput.input.addTextChangedListener(watcherDefault)
                payzeCardNumberInput.input.keyListener = DigitsKeyListener.getInstance(NUMBER_KEYS)
            }
        }
    }

    private fun updateBrandLocally() {
        var brand: String = ""
        val code4 = binding!!.payzeCardNumberInput.getText()?.take(4)

        brand = when {
            code4 == "8600" || code4 == "5614" -> CardBrandType.UZ_CARD.type
            code4 == "9860" -> CardBrandType.HUMO.type
            code4?.take(1) == "4" -> CardBrandType.VISA.type
            (2221..2720).contains(code4?.toInt()) || (51..55).contains(code4?.take(2)?.toInt()) -> CardBrandType.MASTER_CARD.type
            code4?.take(2) == "34" || code4?.take(2) == "37" -> CardBrandType.AMEX.type
            else -> ""
        }

        updateBrand(brand)
    }

    private fun updateBrand(brand: String?) {
        with(binding!!) {
            if (brand.isNullOrEmpty())
                return

            if (cardType == brand)
                return

            payzeCardCodeInput.setText("")

            when (brand) {
                CardBrandType.MASTER_CARD.type -> {
                    updateCardFormat()
                    showCardIcon(iconRes = R.drawable.ic_master_card)
                    payzeCardCodeInput.textLength = 3
                    payzeCardCodeInput.show()
                }

                CardBrandType.VISA.type -> {
                    updateCardFormat()
                    showCardIcon(iconRes = R.drawable.ic_visa)
                    payzeCardCodeInput.textLength = 3
                    payzeCardCodeInput.show()
                }

                CardBrandType.HUMO.type -> {
                    updateCardFormat()
                    payzeCardCodeInput.goAway()
                    payzeCardCodeInput.textLength = 3
                    showCardIcon(iconRes = R.drawable.ic_humo)
                }

                CardBrandType.UZ_CARD.type -> {
                    updateCardFormat()
                    payzeCardCodeInput.textLength = 3
                    payzeCardCodeInput.goAway()
                    showCardIcon(iconRes = R.drawable.ic_uz_card)
                }

                CardBrandType.AMEX.type -> {
                    showCardIcon(iconRes = R.drawable.ic_amex)
                    updateCardFormat(isAmex = true)
                    payzeCardCodeInput.textLength = 4
                    payzeCardCodeInput.show()
                }
            }

            cardType = brand
        }
    }

    private fun isValidLuhn(number: String): Boolean {
        val reversedDigits = number.reversed().map { it.toString().toInt() }
        val checksum = reversedDigits.mapIndexed { index, digit ->
            if (index % 2 == 1) {
                val doubled = digit * 2
                if (doubled > 9) doubled - 9 else doubled
            } else {
                digit
            }
        }.sum()
        return checksum % 10 == 0
    }

    private fun showAlert(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.payze_error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.payze_card_close)) { _, _ -> }
            .show()
    }

    private fun onKeyboardHeightChange(h: Int) {
        if (h > 0) {
            (activity as PayzeActivity).onKeyboardShown(height = h)
            binding?.payzeCardBottomLogo?.goAway()
        } else {
            (activity as PayzeActivity).onKeyboardHidden()
            binding?.payzeCardBottomLogo?.show()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            language: Language,
            transactionId: String,
            companyLogo: Int,
            amount: Money?,
        ) = PayzeCardFragment().apply {
                arguments = bundleOf(
                    COMPANY_LOGO to companyLogo,
                    TRANSACTION_ID to transactionId,
                    AMOUNT to amount,
                    LANGUAGE to language
                )
            }

        private const val COMPANY_LOGO = "comp_logo_res"
        private const val TRANSACTION_ID = "transaction_id"
        private const val AMOUNT = "amount"
        private const val LANGUAGE = "language"
    }
}