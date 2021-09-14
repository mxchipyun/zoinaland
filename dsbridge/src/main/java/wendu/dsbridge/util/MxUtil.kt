package wendu.dsbridge.util

import android.content.Context
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType
import wendu.dsbridge.bean.ConfigBean
import wendu.dsbridge.bean.UserBindRes
import wendu.dsbridge.ext.requestNet
import wendu.dsbridge.ext.runUI
import wendu.dsbridge.inf.DownloadListenerInf
import wendu.dsbridge.net.RetrofitManager
import wendu.dsbridge.net.api.OpenPanelCallback
import wendu.dsbridge.net.api.UpdateRepository
import wendu.dsbridge.net.api.iot.IoTRep
import wendu.dsbridge.net.api.iot.IoTRepFind
import wendu.dsbridge.net.api.iot.IoTRepProvision
import wendu.dsbridge.net.config.ResultCode.Companion.ERROR_EMPTY_RESULT
import java.util.*

object MxUtil {

    private var updateRepository: UpdateRepository? = null
    var routerListener: RouterListener? = null
    private val iotRep = IoTRep()
    private val iotProvision = IoTRepProvision()

    private val downloadListenerInf = DownloadListenerInf()


    //测试环境
    var configBean: ConfigBean? = null


    /**
     * 初始化操作，用来获取面板信息、下载使用
     */
    fun init(
        appKey: String,//后台给的appid
        appSecret: String,//后台给的appSecret
        host: String
    ) {
        configBean = ConfigBean(
            baseUrl = host,
            appKey = appKey,
            appSecret = appSecret
        )
        RetrofitManager.initRetrofit()
        updateRepository = UpdateRepository()
    }


    /**
     * 注册路由回调监听
     */
    fun registerRouterListener(routerListener: RouterListener) {
        this.routerListener = routerListener
    }

    /**
     * 注销路由回调监听
     */
    fun unregisterRouterListener() {
        routerListener = null
    }


    //网关配网
    fun provisionGateway(
        context: Context,
        pk: String,//productKey
        deviceName: String,//deviceName
        successCallback: (userBindRes: UserBindRes?) -> Unit,//成功回调
        failedCallback: (code: String?, msg: String?) -> Unit//失败回调
    ) {
        iotProvision.getToken(context, pk, deviceName, { token ->
            iotRep.userBindByToken(pk, deviceName, token, {
                if (it == null) {
                    failedCallback(ERROR_EMPTY_RESULT, "绑定失败！")
                    return@userBindByToken
                }
                successCallback.invoke(it)
            }, {
                failedCallback.invoke(it?.code.toString(), it?.localizedMsg)
            })

        }, failedCallback)
    }


    //网关子设备接入，请求该接口后，app端等收到回调
    fun provisionGatewaySub(
        iotId: String,//网关设备iotId
        productKey: String,//子设备产品标识符
        time: Int,//0：网关一直允许添加子设备, 0～65535：网关允许添加子设备的时间长度，单位为秒, 65535：网关不允许添加子设备
        failed: (msg: String) -> Unit,
        success: (topicMethod: String, topicData: String, userBindRes: UserBindRes?) -> Unit
    ) {

        iotRep.gatewayPermit(iotId, productKey, time) {
            if (it) {
                downloadListenerInf.addDownStreamListener({ errorMsg ->
                    failed.invoke(errorMsg)
                }, { method, topicData, userBindRes ->
                    success.invoke(method, topicData, userBindRes)
                })
            } else {
                failed.invoke("请求网关子设备接入接口调用失败")
            }
        }

    }

    fun openDevicePanel(
        context: Context,
        iotId: String,
        productKey: String,
        owned: Int,//0（表示被分享的设备）；1（表示拥有的设备）
        callback: ((code: Int, msg: String) -> Unit)? = null//code=0 表示成功
    ) {
        requestNet(errorCallback = {
            callback?.invoke(-1, it)
        }) {
            updateRepository?.openPanel(
                context,
                iotId,
                productKey,
                owned,
                object : OpenPanelCallback {
                    override fun success() {
                        runUI {
                            LogPet.d("openPanel ~~")
                            UpdateRepository.navigateToPanel(context, iotId, productKey, owned)
                            callback?.invoke(0, "open panel success!")
                        }
                    }

                    override fun failed(code: Int, errorMsg: String) {
                        LogPet.d("openPanel error~~")
                        callback?.invoke(code, errorMsg)
                    }
                })
        }
    }


    //蓝牙辅助配网
    private fun provisionBle(
        pk: String,//productKey
        pId: String?,//productId
        wifiName: String?,//wifi ssid
        wifiPwd: String?,//wifi 密码
        provisionCallback: (provisionStatus: Int, userBindRes: UserBindRes?) -> Unit
    ) {
        iotProvision.startProvision(pk, pId, wifiName, wifiPwd, provisionCallback)
    }


    fun startDiscovery(
        enumSet: EnumSet<DiscoveryType>? = null,
        callback: (discoveryType: DiscoveryType, data: List<DeviceInfo>) -> Unit
    ) {
        IoTRepFind().startDiscovery(enumSet, callback)
    }


}

interface RouterListener {
    fun navigate(path: String?, params: String?)
}