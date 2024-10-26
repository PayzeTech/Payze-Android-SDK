package com.payze.sdk.components.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

fun EditText.setAction(imeAction: Int, action: () -> Unit) {
    imeOptions = imeAction
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == imeAction) {
            action()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun View.visibleIf(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.goAway() {
    visibility = View.GONE
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.setMaxLength(maxLength: Int) {
    addInputFilter(InputFilter.LengthFilter(maxLength))
}

fun EditText.addInputFilter(filter: InputFilter) {
    val previousFilters = filters
    val newFiltersArray = Array<InputFilter>(previousFilters.size + 1) {
        if (it < previousFilters.size) {
            previousFilters[it]
        } else {
            filter
        }
    }
    this.filters = newFiltersArray
}