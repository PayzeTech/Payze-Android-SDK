package com.payze.sdk.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.payze.sdk.R
import com.payze.sdk.components.extensions.bottomPadding
import com.payze.sdk.components.extensions.visibleIf
import com.payze.sdk.databinding.ActivityPayzeBinding
import com.payze.sdk.di.IsolatedKoinContext
import com.payze.sdk.di.networkModule
import com.payze.sdk.model.PayzeActivityData
import com.payze.sdk.presentation.card.ui.PayzeCardFragment
import com.payze.sdk.presentation.web_view.ui.PayzeWebFragment
import org.koin.core.context.stopKoin

class PayzeActivity : AppCompatActivity() {

    private var binding: ActivityPayzeBinding? = null
    private var payzeData: PayzeActivityData? = null

    private val fragmentCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            if (f is PayzeWebFragment) {
                updateTitle(isCard = true)
            }
            super.onFragmentDetached(fm, f)
        }
    }

    fun updateTitle(isCard: Boolean) {
        if (isCard) {
            binding?.payzeMainToolbar?.setTitle(R.string.payze_card_title)
            binding?.payzeMainToolbar?.setNavigationIcon(R.drawable.ic_close)
        } else {
            binding?.payzeMainToolbar?.setTitle(R.string.payze_web_title)
            binding?.payzeMainToolbar?.setNavigationIcon(R.drawable.ic_back)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayzeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallback, true)

        binding!!.payzeMainToolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }

        payzeData = intent.getParcelableExtra(PayzeActivityData.KEY_DATA)

        IsolatedKoinContext.koinApp.koin.loadModules(
            listOf(
                networkModule(payzeData?.environment)
            )
        )

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.payzeMainContainer,
                PayzeCardFragment.newInstance(
                    language = payzeData?.language!!,
                    transactionId = payzeData?.transactionId ?: "",
                    companyLogo = payzeData?.companyLogoRes ?: -1,
                    amount = payzeData?.amount
                )
            )
            .commit()
    }

    fun turnLoaderTransparent(show: Boolean) {
        binding?.payzeMainLoader?.visibleIf(show)
    }

    fun onKeyboardShown(height: Int) {
        binding?.payzeMainContainer?.bottomPadding(height)
    }

    fun onKeyboardHidden() {
        binding?.payzeMainContainer?.bottomPadding(0)
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallback)
        stopKoin()
        binding = null
        super.onDestroy()
    }
}