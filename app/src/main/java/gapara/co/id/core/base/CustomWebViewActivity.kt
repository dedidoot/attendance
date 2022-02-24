package gapara.co.id.core.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import gapara.co.id.R
import kotlinx.android.synthetic.main.custom_web_view.*

class CustomWebViewActivity : AppCompatActivity() {

    private val ANDROID = "android"
    private val TXT_HTTP = "http"
    private var url = ""
    private val HEADER_PARAMETER = "webview"
    private val LOCAL_STORAGE = "localStorage.setItem('$HEADER_PARAMETER','$ANDROID');"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_web_view)
        intent?.getStringExtra(EXTRA_URL).takeIf { !it.isNullOrBlank() }?.let {
            url = it
            initWebView()
            setupView()
        } ?: kotlin.run {
            BaseDialogView(this).setMessage("Url tidak ditemukan").setOnClickPositive {
                finish()
            }.show()
        }
    }

    private fun setupView() {
        swipeRefreshWeb.setThemePrimary()

        swipeRefreshWeb.setOnRefreshListener {
            webView.loadUrl(url)
        }

        backImageView.setOnClickListener {
            onBackPressed()
        }

        titlebar.text = intent?.getStringExtra(EXTRA_TITLE_TOOLBAR)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                progressBarWeb.isVisible = true
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressBarWeb.isVisible = false
                webView.evaluateJavascript(LOCAL_STORAGE, null)
                swipeRefreshWeb.isRefreshing = false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url: String = request?.url.toString()
                return handleOverrideUrl(url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return handleOverrideUrl(url)
            }

        }
        val webClient = object : WebChromeClient() {

            override fun onCloseWindow(w: WebView) {
                super.onCloseWindow(w)
                finish()
            }
        }
        webView.clearCache(true)
        webView.webChromeClient = webClient
        webView.loadUrl(url)
    }

    private fun handleOverrideUrl(url: String): Boolean {
        if (url.contains(TXT_HTTP)) {
            return false
        }
        return true
    }

    companion object {

        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE_TOOLBAR = "extra_title_toolbar"

        fun startActivity(activity: Activity, url: String?, titleToolbar : String) {
            val intent = Intent(activity, CustomWebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_TITLE_TOOLBAR, titleToolbar)
            activity.startActivity(intent)
        }
    }
}