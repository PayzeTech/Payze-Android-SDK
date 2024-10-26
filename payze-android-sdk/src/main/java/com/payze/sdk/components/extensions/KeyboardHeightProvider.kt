package com.payze.sdk.components.extensions

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class KeyboardHeightProvider(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private var onKeyboardCallBack: ((Int) -> Unit)?
) : PopupWindow(activity), LifecycleEventObserver {
    
    private val popupView: View = FrameLayout(activity).apply {
        setBackgroundColor(Color.TRANSPARENT)
    }

    private val parentView: View = activity.findViewById(android.R.id.content)

    init {
        contentView = popupView

        softInputMode =
            LayoutParams.SOFT_INPUT_ADJUST_RESIZE or LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        inputMethodMode = INPUT_METHOD_NEEDED

        width = 0
        height = LayoutParams.MATCH_PARENT

        popupView.viewTreeObserver.addOnGlobalLayoutListener {
            handleOnGlobalLayout()
        }

        start()
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> close()
            else -> {}
        }
    }

    private fun start() {
        activity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
        if (!isShowing && parentView.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(0))
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun close() {
        notifyKeyboardHeightChanged(DEFAULT_HEIGHT)
        onKeyboardCallBack = null
        lifecycleOwner.lifecycle.removeObserver(this)
        dismiss()
    }

    private fun handleOnGlobalLayout() {
        val windowRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(windowRect)

        val rect = Rect()
        popupView.getWindowVisibleDisplayFrame(rect)

        val keyboardHeight = windowRect.bottom - rect.bottom

        notifyKeyboardHeightChanged(keyboardHeight)
    }

    private fun notifyKeyboardHeightChanged(height: Int) {
        onKeyboardCallBack?.invoke(height)
    }

    companion object {
        const val DEFAULT_HEIGHT = 0
    }
}
