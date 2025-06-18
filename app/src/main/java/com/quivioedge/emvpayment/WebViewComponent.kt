package com.quivioedge.emvpayment

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.quivioedge.emvpayment.ui.theme.EMVPaymentTheme

class WebViewComponent: ComponentActivity() {
    companion object {
        // Key for passing URL via adb: --es "url" "https://..."
        const val EXTRA_URL = "url"
        const val DEFAULT_URL = "https://www.google.com"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve URL from Intent extras or data URI
        val retrievedUrl: String = intent.getStringExtra(EXTRA_URL)
            ?: intent.dataString
            ?: DEFAULT_URL

        setContent {
            EMVPaymentTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WebViewScreen(
                        url = retrievedUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@Composable
fun WebViewScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    // State to track loading progress
    var progress by remember { mutableStateOf(0) }
    var isError by remember { mutableStateOf(false) }
//    WebView.setWebContentsDebuggingEnabled(true)

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.userAgentString =
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36"

                    webViewClient = object : WebViewClient() {
                        override fun onReceivedError(
                            view: WebView,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            isError = true
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (!isError) webView.loadUrl(url)
            },
            modifier = modifier
        )

        // Show progress indicator
        if (progress in 1..99 && !isError) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
        if (isError) {

        }
    }
}