package wendu.dsbridge.inf

import android.os.Handler
import android.os.Looper
import com.alibaba.fastjson.JSON
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel
import com.google.gson.Gson
import wendu.dsbridge.bean.UserBindRes
import wendu.dsbridge.ext.runDelay
import wendu.dsbridge.net.api.iot.IoTRep
import wendu.dsbridge.util.LogPet.Companion.e

class DownloadListenerInf {

    private val iotRep = IoTRep()

    private var mDownStreamListener: IMobileDownstreamListener? = null
    private val handler = Handler(Looper.getMainLooper())

    fun addDownStreamListener(
        failed: (msg: String) -> Unit,
        success: (method: String, topicData: String, userBindRes: UserBindRes?) -> Unit
    ) {
        if (mDownStreamListener == null)
            mDownStreamListener =
                object : IMobileDownstreamListener {
                    override fun onCommand(method: String, data: String) {
                        e("接收到Topic = $method, data=$data")
                        try {
                            val jsonObject = JSON.parseObject(data)
                            if (method.contains("topo/add/status")) {
                                val newGwProductKey = jsonObject.getString("newGwProductKey")
                                val subProductKey = jsonObject.getString("subProductKey")
                                val subDeviceName = jsonObject.getString("subDeviceName")
                                bindGatewaySub(
                                    subProductKey,
                                    subDeviceName
                                ) { userBindRes: UserBindRes? ->
                                    success.invoke(method, data, userBindRes)
                                    handler.removeCallbacksAndMessages(null)
                                    removeListener()
                                    e("绑定成功：" + Gson().toJson(userBindRes))
                                }
                            }
                        } catch (e: Exception) {
                            e("onCommand parse Object failed!$e")
                            failed.invoke(e.toString())
                        }
                    }

                    override fun shouldHandle(method: String): Boolean {
                        return true
                    }
                }
        MobileChannel.getInstance().registerDownstreamListener(true, mDownStreamListener)
        handler.postDelayed({
            failed.invoke("bind timeout")
            removeListener()
        }, 60 * 1000)
    }

    fun removeListener() {
        if (mDownStreamListener != null) {
            MobileChannel.getInstance().unRegisterDownstreamListener(mDownStreamListener)
        }
    }

    //绑定网关子设备
    private fun bindGatewaySub(
        productKey: String,
        deviceName: String,
        callback: (res: UserBindRes?) -> Unit
    ) {
        runDelay {
            iotRep.userBind(productKey, deviceName, callback)
        }
    }
}