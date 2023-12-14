package com.example.client

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class WebViewActivity : ComponentActivity(), Draw {

    companion object {
        const val ADDRESS = "*****************"
        const val PORT = 8103
        var CAMERA_ID: Int? = null
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        Log.d("get Info!!", "$email, $password")
        webView = setWebView(email, password).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
            )
        }

        setContent {
            Web(webView = webView)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(email: String?, password: String?): WebView {

        val client = object : WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?
            ) {
                handler?.proceed()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (url == "https://${ADDRESS}:${PORT}/Identity/Account/Login") {
                    email?.let {
                        password?.let {
                            view?.loadUrl(
                                "javascript:document.getElementById('Input_Email').value = '$email';" +
                                        "document.getElementById('Input_Password').value='$password';" +
                                        "document.getElementById('login-submit').click();"
                            )
                        }
                    }
                }

                if (url == "https://$ADDRESS:$PORT/" && CAMERA_ID != null) {
                    view?.loadUrl("https://$ADDRESS:$PORT/user/event")
                    CAMERA_ID = null
                }
            }
        }

        val chromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        return WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                mediaPlaybackRequiresUserGesture = false
                databaseEnabled = true
                setSupportMultipleWindows(false)
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
            }
            webViewClient = client
            webChromeClient = chromeClient
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && canGoBack()) {
                    goBack()
                    return@setOnKeyListener true
                } else {
                    return@setOnKeyListener false
                }
            }
            loadUrl("https://$ADDRESS:$PORT/Identity/Account/Login")
        }
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        webView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}