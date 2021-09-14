package wendu.dsbridge.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import wendu.dsbridge.DWebView
import wendu.dsbridge.R
import wendu.dsbridge.ui.panel.DeviceBridgeJsApi
import wendu.dsbridge.ui.panel.IMxchipPanelView
import wendu.dsbridge.ui.panel.PageBridgeJsApi
import wendu.dsbridge.ui.panel.ReqBridgeJsApi
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.SysUtil

class DevicePanelActivity : Activity(), IMxchipPanelView {

    private lateinit var webView: DWebView
    private lateinit var progressBar: ProgressBar

    private var iotId: String? = null
    private var productKey: String? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_panel)
        webView = findViewById(R.id.web_view_panel)
        progressBar = findViewById(R.id.progress_web)
        initWebView()
        intent.extras?.let {
            this.iotId = it.getString("iotId")
            this.productKey = it.getString("productKey")
            this.url = it.getString("url")
        }
        if (iotId.isNullOrEmpty() || url.isNullOrEmpty()) {
            LogPet.e("url or iotId can't be null")
            finish()
            return
        }

        webView.addJavascriptObject(
            PageBridgeJsApi(this@DevicePanelActivity, this),
            "page"
        )
        webView.addJavascriptObject(ReqBridgeJsApi(), "request")
        webView.addJavascriptObject(DeviceBridgeJsApi(this, iotId), "device")
        webView.loadUrl(url)
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                when (newProgress) {
                    100 -> progressBar.visibility = View.GONE
                    else -> {
                        if (progressBar.visibility == View.GONE)
                            progressBar.visibility = View.VISIBLE
                        progressBar.progress = newProgress
                    }
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!webView.settings.loadsImagesAutomatically) {
                    webView.settings.loadsImagesAutomatically = true
                }
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }

        webView.settings.apply {
            val ua = "mxchip app/${SysUtil.getPackageName(this@DevicePanelActivity)}"
//            +" productType/${deviceInfo?.product_variety}"

            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = ua
            javaScriptEnabled = true
            setSupportZoom(true)
            builtInZoomControls = false
            savePassword = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //适配5.0不允许http和https混合使用情况
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            textZoom = 100
            databaseEnabled = true
            //        setAppCacheEnabled(true);
            setSupportMultipleWindows(false)
            // 是否阻塞加载网络图片  协议http or https
            blockNetworkImage = false
            // 允许加载本地文件html  file协议
            allowFileAccess = true
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            allowFileAccessFromFileURLs = true
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            allowUniversalAccessFromFileURLs = true
            javaScriptCanOpenWindowsAutomatically = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                loadsImagesAutomatically = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            } else {
                loadsImagesAutomatically = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            }
            loadWithOverviewMode = false
            useWideViewPort = false
            domStorageEnabled = true
            setNeedInitialFocus(true)
            defaultTextEncodingName = "utf-8" //设置编码格式

            defaultFontSize = 16
            minimumFontSize = 12 //设置 WebView 支持的最小字体大小，默认为 8

            setGeolocationEnabled(true)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val locaUrl = "file:///android_asset/request_failed/reLoad.html"
        if (webView.canGoBack()
            && webView.url != locaUrl
        ) {
            val mWebBackForwardList: WebBackForwardList = webView.copyBackForwardList()
            if (mWebBackForwardList.currentIndex > 0) {
                val historyUrl = mWebBackForwardList.getItemAtIndex(
                    mWebBackForwardList.currentIndex - 1
                ).url
                if (historyUrl == locaUrl) {
                    finish()
                    return
                }
                webView.goBack()
            }
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        super.onDestroy()
    }

    override fun finishActivity() {
        finish()
    }

    override fun setTitle(title: String?) {

    }


}