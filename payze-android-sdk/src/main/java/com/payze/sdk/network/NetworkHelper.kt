package com.payze.sdk.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall.invoke()
            if ((response as Response<*>).body() != null) {
                ResultWrapper.Success(response)
            } else {
                throw HttpException((response as Response<*>))
            }
        } catch (throwable: Throwable) {
            when  {
                throwable is IOException -> ResultWrapper.NetworkError
                throwable is HttpException -> {
                    ResultWrapper.GenericError(throwable.code(), "General error")
                }
                else -> {
                    ResultWrapper.GenericError(null, "General error")
                }
            }
        }
    }
}