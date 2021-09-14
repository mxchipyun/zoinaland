package wendu.dsbridge.net.api

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import wendu.dsbridge.H5Load.CallBackH5
import wendu.dsbridge.H5Load.H5LocalCacheUtil
import wendu.dsbridge.H5Load.H5ModuleEntranceUtil
import wendu.dsbridge.bean.panel.H5BeanReq
import wendu.dsbridge.bean.panel.H5Result
import wendu.dsbridge.net.RetrofitManager
import wendu.dsbridge.net.config.ServiceConfig
import wendu.dsbridge.ui.DevicePanelActivity
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.MxUtil

class UpdateRepository {

    companion object {
        fun navigateToPanel(context: Context, iotId: String, productKey: String, owned: Int) {
            val url = H5LocalCacheUtil.getH5Url(productKey, iotId, owned)
            if (url.isNullOrEmpty()) {
                LogPet.e("url is null or empty")
                return
            }
            context.startActivity(
                Intent(
                    context,
                    DevicePanelActivity::class.java
                ).apply {
                    putExtra("iotId", iotId)
                    putExtra("url", url)
                })
        }
    }


    //打开设备面板
    suspend fun openPanel(
        context: Context,
        iotId: String,
        productKey: String,
        owned: Int,
        callback: OpenPanelCallback
    ) {

        val ts = (System.currentTimeMillis() / 1000).toString()
        val noce = RetrofitManager.getRandomString()
//        val version = SpUtil.getString(context, SpKey.H5_VERSION)
        val bean = H5BeanReq(
            product_key = productKey,
            version = null,
            ts = ts,
            appid = MxUtil.configBean?.appKey ?: "",
            nonce = noce,
            sign = RetrofitManager.bit32("${ServiceConfig.getConfig()?.appKey},${ServiceConfig.getConfig()?.appSecret},${noce},$ts")
        )

        RetrofitManager.getService(UpdateService::class.java)?.panelH5UpgradeInfo(bean).let {
            if (it == null) {
                LogPet.e("result is null")
                navigateToPanel(context, iotId, productKey, owned)
                return@let
            }
            if (it.code != 0) {
                callback.failed(code = it.code, errorMsg = it.message)
                return
            }
            val pannel = it?.convert()
            if (pannel == null) {
                LogPet.e("pannel bean is null")
                callback.failed(it.code, it.message)
                return@let
            }
            pannel.productKey = productKey
            LogPet.d("load h5,result:" + JsonUtil.toJson(it))
            H5ModuleEntranceUtil.entranceH5(pannel, object : CallBackH5 {

                override fun onBefore() {
//                    showLoading()
                }

                override fun onAfter() {
//                    dismissLoading()
                }

                override fun onResponse(result: H5Result) {
                    if (result.isSuccess) {
                        callback.success()
                    } else {
                        ToastUtils.showShort(result.message)
                    }
                }

                override fun onError() {
//                    dismissLoading()
                }

            })
        }
    }

}

interface OpenPanelCallback {
    fun success()
    fun failed(code: Int, errorMsg: String)
}