package com.payze.sdk.components.formatter

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    private inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {
        override fun get(index: Int): Char {
            return '*' // This is the important part
        }

        override val length: Int
            get() = source.length // Return default

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex) // Return default
        }
    }
}