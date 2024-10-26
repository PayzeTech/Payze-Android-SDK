package com.payze.sdk.presentation.card.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payze.sdk.components.extensions.SingleLiveEvent
import com.payze.sdk.network.ResultWrapper
import com.payze.sdk.network.model.PayzeCardBrandResponse
import com.payze.sdk.network.model.PayzePayRequest
import com.payze.sdk.network.model.PayzePayResponse
import com.payze.sdk.network.repository.card.PayzeCardRepository
import kotlinx.coroutines.launch

class PayzeCardVm(
    private val cardRepository: PayzeCardRepository
) : ViewModel() {

    val brand = SingleLiveEvent<PayzeCardBrandResponse?>()
    val pay = SingleLiveEvent<PayzePayResponse?>()
    val error = SingleLiveEvent<Unit>()
    val brandError = SingleLiveEvent<Pair<String, Boolean>>()

    fun getCardBrand(cardNumber: String) {
        viewModelScope.launch {
            when (val cardBrand = cardRepository.getCardBrand(cardNumber)) {
                is ResultWrapper.NetworkError -> brandError.value = Pair("General error", false)
                is ResultWrapper.GenericError -> brandError.value = Pair(cardBrand.errorMessage ?: "", false)
                is ResultWrapper.Success -> brand.value = cardBrand.value.body()
            }
        }
    }

    fun pay(
        transactionId: String,
        number: String,
        cardHolder: String,
        expDate: String,
        secNumber: String
    ) {
        viewModelScope.launch {
            when (val payResponse = cardRepository.pay(
                PayzePayRequest(
                    transactionId = transactionId,
                    number = number,
                    cardHolder = cardHolder,
                    expirationDate = expDate,
                    securityNumber = secNumber
                )
            )) {
                is ResultWrapper.NetworkError -> error.value = Unit
                is ResultWrapper.GenericError -> error.value = Unit
                is ResultWrapper.Success -> pay.value = payResponse.value.body()
            }
        }
    }
}