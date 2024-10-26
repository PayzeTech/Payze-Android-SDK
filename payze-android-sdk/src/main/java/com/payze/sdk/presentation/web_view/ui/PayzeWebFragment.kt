package com.payze.sdk.presentation.web_view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.payze.sdk.BuildConfig
import com.payze.sdk.databinding.PayzeWebFragmentBinding
import com.payze.sdk.manager.Payze.Companion.RETURN_DATA
import com.payze.sdk.model.PayzeResult
import com.payze.sdk.presentation.PayzeActivity
import com.payze.sdk.presentation.web_view.vm.PayzeWebVm
import org.koin.androidx.viewmodel.ext.android.viewModel

class PayzeWebFragment : Fragment() {

    private var binding: PayzeWebFragmentBinding? = null
    private val viewModel: PayzeWebVm by viewModel()

    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PayzeWebFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding!!) {
            url = arguments?.getString(TRANSACTION_URL) ?: ""

            payzeWebView.settings.javaScriptEnabled = true
            payzeWebView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)


                }
            }
            payzeWebView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (checkRedirect(request?.url.toString())) return true
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    (context as? PayzeActivity)?.turnLoaderTransparent(show = false)
                    super.onPageFinished(view, url)
                }
            }
            payzeWebView.loadUrl(url)
        }
    }

    private fun checkRedirect(url: String?): Boolean {
        if (
            url?.contains(CHECK_LINK_1) == true ||
            url?.contains(CHECK_LINK_2) == true ||
            url?.contains(CHECK_LINK_3) == true
            )
        {
            handleRedirect(url)
        }
        return false
    }

    private fun handleRedirect(url: String?) {
        when {
            url?.contains(KEY_SUCCESS, true) == true -> {
                (context as PayzeActivity).apply {
                    intent.putExtra(RETURN_DATA, PayzeResult.SUCCESS.value)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            url?.contains(KEY_IN_PROGRESS, true) == true -> {
                (context as PayzeActivity).apply {
                    intent.putExtra(RETURN_DATA, PayzeResult.IN_PROGRESS.value)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            url?.contains(KEY_FAIL, true) == true -> {
                (context as PayzeActivity).apply {
                    intent.putExtra(RETURN_DATA, PayzeResult.FAIL.value)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        (activity as PayzeActivity).updateTitle(isCard = false)
        super.onResume()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(
            url: String
        ) = PayzeWebFragment().apply {
            arguments = bundleOf(
               TRANSACTION_URL to url,
            )
        }

        private const val KEY_FAIL = "Fail"
        private const val KEY_SUCCESS = "Success"
        private const val KEY_IN_PROGRESS = "InProgress"
        private const val TRANSACTION_URL = "url_transaction"
        private const val CHECK_LINK_1 = "https://paygate.payze.dev/"
        private const val CHECK_LINK_2 = "https://paygate.payze.io/"
        private const val CHECK_LINK_3 = "https://paygate.payze.uz/"

    }
}