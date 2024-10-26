package com.payze.sdk.components.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.payze.sdk.R
import com.payze.sdk.components.extensions.goAway
import com.payze.sdk.components.extensions.onChange
import com.payze.sdk.components.extensions.setAction
import com.payze.sdk.components.extensions.setMaxLength
import com.payze.sdk.components.extensions.show
import com.payze.sdk.components.extensions.visibleIf
import com.payze.sdk.databinding.PayzeEditTextWithTitleViewBinding

@Suppress("SetterBackingFieldAssignment")
class EditTextWithTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding by lazy { PayzeEditTextWithTitleViewBinding.inflate(LayoutInflater.from(context), this, true) }

    var onTextChanged: ((newValue: String) -> Unit)? = null

    val input: EditText
        get() {
            return binding.editTextWithTitleInput
        }

    var title: String? = null
        set(value) {
            with(binding) {
                if (value == null)
                    editTextWithTitleTitle.visibility = View.GONE
                else {
                    editTextWithTitleTitle.visibility = View.VISIBLE
                    editTextWithTitleTitle.text = value
                }
            }
            field = value
        }

    var hint: String? = ""
        set(value){
            field = value
            with(binding) {
                value?.let { editTextWithTitleInput.hint = it }
            }
        }

    var bottomMessage: String? = null
        set(value) {
            with(binding) {
                if (value != null) {
                    editTextWithTitleBottomMessage.text = value
                    editTextWithTitleBottomMessage.visibility = View.GONE
                }
            }
        }

    var inputValue: String?
        get() {
            return binding.editTextWithTitleInput.text?.toString()
        }
        set(value) {
            binding.editTextWithTitleInput.setText(value)
        }

    var whiteBackground: Boolean? = false
        set(value) {
            field = value
            if (value == true)
                binding.editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input)
        }

    private var watcher: TextWatcher? = null

    var textType: Int? = null
        set(value: Int?) {
            if (value == -1) return
            with(binding) {
                when (value) {
                    TYPE_CLASS_TEXT -> editTextWithTitleInput.inputType = InputType.TYPE_CLASS_TEXT
                    TYPE_CLASS_NUMBER -> editTextWithTitleInput.inputType = InputType.TYPE_CLASS_NUMBER
                    TYPE_NUMBER_FLAG_DECIMAL -> editTextWithTitleInput.inputType =
                        (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    TYPE_TEXT_VARIATION_PASSWORD -> editTextWithTitleInput.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                    TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> editTextWithTitleInput.inputType =
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    TYPE_CLASS_PHONE -> editTextWithTitleInput.inputType =
                        InputType.TYPE_CLASS_PHONE
                    TYPE_AMOUNT_DECIMAL -> {
                        editTextWithTitleInput.inputType =
                            (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                    }
                    TYPE_AMOUNT_INTEGER -> {
                        editTextWithTitleInput.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
            }

            field = value
        }

    var imeOption: Int? = null
        set(value: Int?) {
            if (value == -1) return
            with(binding) {
                when (value) {
                    IME_ACTION_GO -> editTextWithTitleInput.imeOptions = EditorInfo.IME_ACTION_GO
                    IME_ACTION_DONE -> editTextWithTitleInput.imeOptions =
                        EditorInfo.IME_ACTION_DONE
                    IME_ACTION_NEXT -> editTextWithTitleInput.imeOptions =
                        EditorInfo.IME_ACTION_NEXT
                    IME_ACTION_SEARCH -> editTextWithTitleInput.imeOptions =
                        EditorInfo.IME_ACTION_SEARCH
                }
            }
            field = value
        }

    var imeAction: (() -> Unit)? = null
        set(value) {
            field = value
            if (imeOption == null) throw RuntimeException("imeOptionMustBeSet")
            if (field != null) {
                binding.editTextWithTitleInput.setAction(binding.editTextWithTitleInput.imeOptions, value!!)

            }
        }

    @ColorInt
    var textColor: Int = -1
        set(value) {
            field = value
            if (value == -1) return
            binding.editTextWithTitleInput.setTextColor(value)
        }


    @ColorInt
    var bottomHintColor: Int = -1
        set(value) {
            if (value == -1) return
            binding.editTextWithTitleBottomMessage.setTextColor(value)
        }

    @ColorInt
    var hintTextColor: Int = -1
        set(value) {
            if (value == -1) return
            binding.editTextWithTitleInput.setHintTextColor(value)
        }

    var textLength: Int = -1
        set(value) {
            field = value
            if (value == -1) return
            if (textType != TYPE_AMOUNT_DECIMAL && textType != TYPE_AMOUNT_INTEGER)
                binding.editTextWithTitleInput.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(value))
        }


    var editable = true
        set(value) {
            field = value
            binding.editTextWithTitleInput.isEnabled = value
        }

    var rightActionDrawableRes: Int? = null
        @SuppressLint("UseCompatLoadingForDrawables")
        set(value) {
            rightActionDrawable = value?.let { resources.getDrawable(it, null) }
        }

    var actionClick: (() -> Unit)? = null

    var rightActionDrawable: Drawable? = null
        set(value) {
            with(binding) {
                editTextWithTitleImage.setImageDrawable(value)
                editTextWithTitleImage.visibleIf(value != null)
            }
        }

    var isDrawableVisible: Boolean = false
        set(value) {
            binding.editTextWithTitleImage.visibleIf(value)
            field = value
        }

    var remainingHint: Boolean = false

    var onRightDrawableClickListener: OnClickListener? = null
        set(value) {
            binding.editTextWithTitleImage.setOnClickListener(value)
        }

    var isError: Boolean = false

    var isDefaultMode: Boolean = true
        set(value) {
            field = value
            defaultMode()
        }

    init {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.EditTextWithTitle, 0, 0)

            title = typedArray.getString(R.styleable.EditTextWithTitle_inputTitle)
            binding.editTextWithTitleTitle.visibleIf(
                typedArray.getBoolean(
                    R.styleable.EditTextWithTitle_inputTitleShow,
                    true
                )
            )

            hint = typedArray.getString(R.styleable.EditTextWithTitle_inputHint)

            bottomMessage =
                typedArray.getString(R.styleable.EditTextWithTitle_inputBottomMessage)

            textLength =
                typedArray.getInt(R.styleable.EditTextWithTitle_inputTextLength, -1)

            imeOption = typedArray.getInt(R.styleable.EditTextWithTitle_inputImeOption, -1)

            textColor = typedArray.getInt(R.styleable.EditTextWithTitle_inputTextColor, -1)
            bottomHintColor =
                typedArray.getInt(R.styleable.EditTextWithTitle_inputBottomHintColor, -1)

            textType = typedArray.getInt(R.styleable.EditTextWithTitle_inputTextType, -1)

            editable =
                typedArray.getBoolean(R.styleable.EditTextWithTitle_inputEditable, true)

            rightActionDrawable =
                typedArray.getDrawable(R.styleable.EditTextWithTitle_inputRightActionIcon)

            whiteBackground =
                typedArray.getBoolean(R.styleable.EditTextWithTitle_inputWhiteBackground,false)


            hintTextColor =
                typedArray.getInt(R.styleable.EditTextWithTitle_inputHintTextColor, -1)

            with(binding) {
                editTextWithTitleInput.id = View.generateViewId()
            }

            typedArray.recycle()
        }

        input.onChange {
            onTextChanged?.invoke(it)
        }

        input.setOnFocusChangeListener { v, hasFocus ->
            with(binding) {
                if (hasFocus) {
                    if(whiteBackground != true) {
                        editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input_focused)
                    }
                }
                else {
                    if(whiteBackground != true) {
                        if (input.text.isNullOrEmpty() || isError) {
                            editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input_error)
                            editTextWithTitleBottomMessage.show()
                        } else {
                            editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input)
                            editTextWithTitleBottomMessage.goAway()
                        }
                    }
                }
            }
        }
    }

    private fun defaultMode() {
        with(binding) {
            if (isDefaultMode) {
                if (hasFocus()) {
                    editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input_focused)
                    editTextWithTitleBottomMessage.goAway()
                } else {
                    editTextWithTitleContainer.setBackgroundResource(R.drawable.bkg_edit_text_input)
                    editTextWithTitleBottomMessage.goAway()
                }
            }
        }
    }

    fun focus() {
        binding.editTextWithTitleContainer.requestFocus()
    }

    fun setEditTextMaxLength(maxLength: Int){
        binding.editTextWithTitleInput.setMaxLength(maxLength)
    }

    fun setOnCLickListener(onclick: () -> Unit) {
        with(binding) {
            editTextWithTitleInput.setOnClickListener { onclick.invoke() }
        }
    }

    fun setListener(callBack: ((String) -> Unit)?) {
        binding.editTextWithTitleInput.onChange {
            callBack?.invoke(it)
        }
    }

    companion object {
        const val TYPE_CLASS_TEXT = 0
        const val TYPE_CLASS_NUMBER = 1
        const val TYPE_NUMBER_FLAG_DECIMAL = 2
        const val TYPE_TEXT_VARIATION_PASSWORD = 3
        const val TYPE_TEXT_VARIATION_EMAIL_ADDRESS = 4
        const val TYPE_CLASS_PHONE = 5
        const val TYPE_AMOUNT_INTEGER = 6
        const val TYPE_AMOUNT_DECIMAL = 7

        const val IME_ACTION_GO = 0
        const val IME_ACTION_DONE = 1
        const val IME_ACTION_NEXT = 2
        const val IME_ACTION_SEARCH = 3

    }

    fun setInputLength(length: Int) {
        textLength = length
    }

    fun getText(): String? {
        return inputValue
    }

    fun setText(text: String?) {
        if (inputValue != text) inputValue = text
    }

    fun clearSetText(text: String?) {
        inputValue = text
    }
}