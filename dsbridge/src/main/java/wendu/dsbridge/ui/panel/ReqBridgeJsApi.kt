package wendu.dsbridge.ui.panel

import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import com.alibaba.fastjson.JSON
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder
import org.json.JSONException
import org.json.JSONObject
import wendu.dsbridge.CompletionHandler
import wendu.dsbridge.bean.BaseResp
import wendu.dsbridge.net.FetchCallback
import wendu.dsbridge.net.ProductRepository
import wendu.dsbridge.net.config.ServiceConfig.Companion.getConfig
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.LogPet

class ReqBridgeJsApi {
    val TAG = "RequestBridgeJsApi"

    /**
     * 介绍：设备解绑接口
     * 传参：params:{"iotId":"","homeId":""}
     * 版本：1.0.7
     */
    val HOME_DEVICE_UNBIND = "/uc/unbindAccountAndDev"
    val HOME_DEVICE_UNBIND_VERSION = "1.0.8"

    /**
     * 网关子设备解绑
     */
    val HOME_DEVICE_SUB_UNBIND = "/awss/subdevice/unbind"
    val HOME_DEVICE_SUB_UNBIND_VERSION = "1.0.7"

    @JavascriptInterface
    fun fetch(msg: Any, handler: CompletionHandler<Any>) {
        LogPet.d(msg.toString())
        val msgObj = JSON.parseObject(msg.toString())
        val paramsObj = msgObj.getJSONObject("data")
        val request = IoTRequestBuilder()
            .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
            .setPath(msgObj.getString("url")) // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
            .setApiVersion(msgObj.getString("version")) // 参照API文档，设置API接口的版本号，本示例为1.0.2
            .setAuthType(msgObj.getString("authType")) //          .addParam("input", "测试") // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
            .setParams(paramsObj.innerMap)
            .build()

        // 获取Client实例，并发送请求
        val ioTAPIClient = IoTAPIClientFactory().client
        ioTAPIClient.send(request, object : IoTCallback {
            override fun onFailure(request: IoTRequest, e: Exception) {
                // TODO根据e，处理异常
//                Log.d(TAG+request.getPath(),e.toString());
                e.printStackTrace()
                LogPet.e("e:$e")
            }

            override fun onResponse(request: IoTRequest, response: IoTResponse) {
//                Log.d(TAG + request.getPath(), response.getData()!=null ? response.getData().toString():"data is null");
                val code = response.code
                val url = msgObj.getString("url")
                // TODO: 2020/10/22 这个判断后续要调整
                // 200 代表成功
                try {
                    if (code != 200) {
                        val jsonData = JSONObject()
                        jsonData.put("code", code)
                        if (TextUtils.isEmpty(response.localizedMsg)) jsonData.put(
                            "message",
                            response.message
                        ) else jsonData.put("message", response.localizedMsg)
                        handler.complete(jsonData)
                    } else {
                        if (HOME_DEVICE_UNBIND.equals(url, ignoreCase = true)
                            || HOME_DEVICE_SUB_UNBIND.equals(
                                url,
                                ignoreCase = true
                            )
                        ) {
                            // TODO 解绑后，更新设备列表
//                            val refreshMyDeviceEvent =
//                                RefreshMyDeviceEvent(paramsObj.getString("iotId"))
//                            EventBus.getDefault().postSticky(refreshMyDeviceEvent)
                        }
                        //  2020/12/11 EventBus.getDefault().post(new UpdateDeviceNameEvent(iotId, name));
                        val jsonData = JSONObject()
                        jsonData.put("code", code)
                        if (response.data != null && !TextUtils.isEmpty(response.data.toString())) {
                            val data = JSONObject(response.data.toString())
                            jsonData.put("data", data)
                        }
                        Log.d(TAG, "fetch--complete->$jsonData")
                        handler.complete(jsonData)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * @param msg
     * @param handler
     */
    @JavascriptInterface
    fun fetchOwnServer(msg: Any, handler: CompletionHandler<Any>) {
        LogPet.d(TAG + "fetchOwnServer=" + msg.toString())
        val msgObj = JSON.parseObject(msg.toString())
        val paramsObj = msgObj.getJSONObject("data")
        val method = msgObj.getString("method")
        val url = getConfig()?.baseUrl + "/" + msgObj.getString("url")
        if (method == "GET") {
            LogPet.d("GET")
            onGetRequest(paramsObj.innerMap, url, handler)
        } else if (method == "POST") {
            LogPet. d("POST")
            onPOSTRequest(paramsObj.innerMap, url, handler)
        } else if (method == "PUT") {
            LogPet. d("PUT")
            onPUTRequest(paramsObj.innerMap, url, handler)
        } else if (method == "DELETE") {
            LogPet. d("DELETE")
            onDELETERequest(paramsObj.innerMap, url, handler)
        }
    }

    private fun onSuccees(url: String, `object`: BaseResp<Any>?, handler: CompletionHandler<Any>) {
        LogPet.d(`object`.toString())
        when {
            url.contains("app/v1/device/updateNickname") -> {
                //修改昵称
//                EventBus.getDefault().post(RefreshMyDeviceEvent())
            }
        }
        if (null == `object`?.data) {
            return
        }
        try {
            val jsonData = JSONObject(JsonUtil.toJson(`object`))
            handler.complete(jsonData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val productRepository = ProductRepository()

    private fun onDELETERequest(
        hashMap: Map<String, Any>,
        url: String,
        handler: CompletionHandler<Any>
    ) {
        LogPet.d(TAG + "method=onDELETERequest," + hashMap.toString())
        productRepository.fetchOwnServerDELETE(url, hashMap, object : FetchCallback {
            override fun onSuccess(obj: BaseResp<Any>?) {
                onSuccees(url, obj, handler)
            }
        })
    }


    private fun onPUTRequest(
        hashMap: Map<String, Any>,
        url: String,
        handler: CompletionHandler<Any>
    ) {
        LogPet.d(TAG + "method=onPUTRequest," + hashMap.toString())
        productRepository.fetchOwnServerPUT(url, hashMap, object : FetchCallback {
            override fun onSuccess(obj: BaseResp<Any>?) {
                onSuccees(url, obj, handler)
            }
        })
    }

    private fun onPOSTRequest(
        hashMap: Map<String, Any>,
        url: String,
        handler: CompletionHandler<Any>
    ) {
        LogPet.d(TAG + "method=onPOSTRequest," + hashMap.toString())
        productRepository.fetchOwnServerPOST(url, hashMap, object : FetchCallback {
            override fun onSuccess(obj: BaseResp<Any>?) {
                onSuccees(url, obj, handler)
            }
        })
    }

    private fun onGetRequest(
        hashMap: Map<String, Any>,
        url: String,
        handler: CompletionHandler<Any>
    ) {
        LogPet.d(TAG + "method=onGetRequest," + hashMap.toString())
        productRepository.fetchOwnServerGET(url, hashMap, object : FetchCallback {
            override fun onSuccess(obj: BaseResp<Any>?) {
                onSuccees(url, obj, handler)
            }
        })
    }

}