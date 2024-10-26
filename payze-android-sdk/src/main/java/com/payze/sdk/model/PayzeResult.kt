package com.payze.sdk.model

enum class PayzeResult(val value: Int) {
    IN_PROGRESS(1),
    SUCCESS(2),
    FAIL(3);

    companion object {
        fun fromValue(id: Int?): PayzeResult {
            for (type in values()) {
                if (type.value == id) {
                    return type
                }
            }
            return IN_PROGRESS
        }
    }
}