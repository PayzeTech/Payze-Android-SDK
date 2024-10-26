package com.payze.sdk.components.extensions

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.payze.sdk.R
import java.util.concurrent.atomic.AtomicBoolean

fun Button.disableBtn() {
    this.setBackgroundResource(R.drawable.bkg_action_disabled_btn)
}

fun Button.enableBtn() {
    this.setBackgroundResource(R.drawable.bkg_action_btn)
}

fun Activity.hideKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

fun View.bottomPadding(value: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, value)
}

fun View.onSafeClick(
    duration: Long = 500,
    click: ((v: View?) -> Unit)
) {
    setOnClickListener(object : DebouncingOnClickListener(duration) {
        override fun doClick(v: View?) {
            click.invoke(v)
        }
    })
}

abstract class DebouncingOnClickListener(private val duration: Long) : View.OnClickListener {
    override fun onClick(v: View) {
        if (!enabled && System.currentTimeMillis() - lastTimeFrame < duration) {
            lastTimeFrame = System.currentTimeMillis()
            MAIN.removeCallbacks(ENABLE_AGAIN)
            MAIN.postDelayed(ENABLE_AGAIN, duration)
            return
        }
        if (enabled && !isTransitioning) {
            enabled = false

            lastTimeFrame = System.currentTimeMillis()
            MAIN.postDelayed(ENABLE_AGAIN, duration)
            doClick(v)
        }
    }

    abstract fun doClick(v: View?)

    companion object {
        var isTransitioning = false
            set(value) {
                field = value
                MAIN.removeCallbacks(ENABLE_AGAIN)
                if (!field) enabled = true
            }
        private val ENABLE_AGAIN = java.lang.Runnable { enabled = true }
        private val MAIN: Handler = Handler(Looper.getMainLooper())
        private var enabled = true
        private var lastTimeFrame = 0L
    }
}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Observe the internal MutableLiveData
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
}