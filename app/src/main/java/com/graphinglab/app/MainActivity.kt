package com.graphinglab.app

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout

@Suppress("DEPRECATION")
class MainActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val headerColor = Color.rgb(23, 61, 154)
        val footerColor = Color.rgb(19, 35, 61)
        val pageColor = Color.rgb(245, 248, 252)

        // على أندرويد 15 (targetSdk 35) يمتد التطبيق خلف شريطَي النظام تلقائيًا،
        // لذلك نضيف شريطين بلونَي الواجهة بارتفاع الحواف حتى لا يغطي النظام المحتوى.
        val topBar = View(this).apply { setBackgroundColor(headerColor) }
        val bottomBar = View(this).apply { setBackgroundColor(footerColor) }

        webView = WebView(this).apply {
            setBackgroundColor(pageColor)
            overScrollMode = View.OVER_SCROLL_NEVER
            settings.javaScriptEnabled = true
            settings.textZoom = 100
            settings.displayZoomControls = false
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(pageColor)
            addView(
                topBar,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            )
            addView(
                webView,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            )
            addView(
                bottomBar,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            )
        }

        root.setOnApplyWindowInsetsListener { _, insets ->
            val top: Int
            val bottom: Int
            val left: Int
            val right: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bars = insets.getInsets(
                    WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout()
                )
                // لوحة المفاتيح لا تُحسب إلا على أندرويد 15+ (حيث لا يُغيَّر حجم النافذة تلقائيًا).
                val imeBottom = if (Build.VERSION.SDK_INT >= 35) {
                    insets.getInsets(WindowInsets.Type.ime()).bottom
                } else {
                    0
                }
                top = bars.top
                left = bars.left
                right = bars.right
                bottom = maxOf(bars.bottom, imeBottom)
            } else {
                top = insets.systemWindowInsetTop
                left = insets.systemWindowInsetLeft
                right = insets.systemWindowInsetRight
                bottom = insets.systemWindowInsetBottom
            }
            topBar.layoutParams.height = top
            bottomBar.layoutParams.height = bottom
            topBar.requestLayout()
            bottomBar.requestLayout()
            root.setPadding(left, 0, right, 0)
            insets
        }

        setContentView(root)

        // للأجهزة التي لا تمتد خلف الشريطين (أندرويد 14 وما دونه).
        window.statusBarColor = headerColor
        window.navigationBarColor = footerColor

        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
