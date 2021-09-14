package wendu.dsbridge.net.api.iot

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder
import com.google.gson.Gson
import wendu.dsbridge.bean.BaseIoTReq
import wendu.dsbridge.util.LogPet

/**
 * 调用IoT平台的接口
 *
 */
object IotApi {

    val ioTClient = IoTAPIClientFactory().client

    //网关子设备接入
    val gatewayPermit = BaseIoTReq("1.0.2", "/thing/gateway/permit")

    //获取支持的设备列表
    val supportDeviceReq = BaseIoTReq("1.1.3", "/thing/productInfo/getByAppKey")

    val getBindingByAccount = BaseIoTReq("1.0.8", "/uc/listBindingByAccount")

    //获取产品productKey
    val getPKByPidReq = BaseIoTReq("1.1.4", "/thing/productInfo/queryProductKey")

    //获取产品信息通过productKey
    val getProductInfoByPkReq = BaseIoTReq("1.1.4", "/thing/allProductInfo/getByProductKey")

    //基于时间窗口方式的绑定设备,根据时间窗口（600秒）校验并绑定设备，从设备上电开始计算
    val userBindReq = BaseIoTReq("1.0.8", "/awss/time/window/user/bind")

    //    val userBindReq = BaseIoTReq("1.0.2", "awss/enrollee/user/bind")//老接口
    //基于token方式设备绑定
    val userBindByTokenReq = BaseIoTReq("1.0.8", "/awss/token/user/bind")

    //获取物的基本信息
    val infoByIotReq = BaseIoTReq("1.0.4", "/thing/info/get")

    //获取物的属性
    val propertiesReq = BaseIoTReq("1.0.4", "/thing/properties/get")

    //设备分享给指定的用户
    val shareDevicesAndScenesReq = BaseIoTReq("1.0.8", "/uc/shareDevicesAndScenes")

    //管理员解绑设备
    val unbindByManagerReq = BaseIoTReq("1.0.6", "/uc/unbindByManager")

    //解绑用户和设备
    val unbindAccountAndDevReq = BaseIoTReq("1.0.8", "/uc/unbindAccountAndDev")

    //获取固件升级进度
    val otaProgressReq = BaseIoTReq("1.0.0", "/living/ota/progress/get")

    //确认固件升级
    val otaConfirmReq = BaseIoTReq("1.0.0", "/living/ota/confirm")

    //取消固件升级
    val otaCancelReq = BaseIoTReq("1.0.0", "/living/ota/cancel")


    //查看固件版本号
    val otaVersionOld = BaseIoTReq("1.0.2", "/thing/ota/info/queryByUser")

    val otaVersion = BaseIoTReq("1.0.0", "/living/ota/firmware/file/get")

    //获取固件升级进度
    val otaProgressReqOld = BaseIoTReq("1.0.2", "/thing/ota/progress/getByUser")

    //确认固件升级
    val otaConfirmReqOld = BaseIoTReq("1.0.2", "/thing/ota/batchUpgradeByUser")

    //取消固件升级
    val otaCancelReqOld = BaseIoTReq("1.0.2", "/living/ota/cancel")

    //消息中心消息列表接口
    val recordQueryReq = BaseIoTReq("1.0.6", "/message/center/record/query")
//    val recordQueryReq = BaseIoTReq("1.0.1", "/message/center/query/push/message")

    //获取共享通知列表
    val shareNoticeList = BaseIoTReq("1.0.8", "/uc/getShareNoticeList")

    //清空共享消息列表,调用该接口清空共享消息列表，包括本人发起和接收的全部消息。
    val clearShareNotice = BaseIoTReq("1.0.6", "/uc/clearShareNoticeList")

    //被分享者同意或拒绝分享
    val confirmShareReq = BaseIoTReq("1.0.7", "/uc/confirmShare")


    //根据设备获取绑定关系
    val listBindingByDevReq = BaseIoTReq("1.0.6", "/uc/listBindingByDev")

    //删除用户的消息记录
    val deleteRecordReq = BaseIoTReq("1.0.7", "/message/center/record/delete")

    //标记全部已读用户的消息记录
    val allUpdateReq = BaseIoTReq("1.0.0", "/message/center/message/allupdate")

    //更新当前用户的消息记录
    val recordModifyReq = BaseIoTReq("1.0.10", "/message/center/record/modify")


    //设置设备告警提醒配置
    val noticeSetReq = BaseIoTReq("1.0.7", "/message/center/device/global/notice/set")

    //获取设备告警全局提醒配置
    val noticeGetReq = BaseIoTReq("1.0.7", "/message/center/device/global/notice/get")

    //统计用户的消息记录数量
    val deviceMsgCountReq = BaseIoTReq("1.0.6", "/message/center/record/messagetype/count")

    //根据code绑定淘宝账号
    val taoBaoBindReq = BaseIoTReq("1.0.5", "/account/taobao/bind")

    //解除绑定淘宝账号
    val taoBaoUnBindReq = BaseIoTReq("1.0.5", "/account/thirdparty/unbind")

    //判断是否已绑定
    val taoBaoHadBindReq = BaseIoTReq("1.0.5", "/account/thirdparty/get")


    fun createIoTReq(baseIoTReq: BaseIoTReq): IoTRequest {
        return IoTRequestBuilder()
            .setPath(baseIoTReq.path)
            .setScheme(Scheme.HTTPS)
            .setApiVersion(baseIoTReq.version)
            .setAuthType("iotAuth")
            .build()
    }

    fun createIoTBuilder(baseIoTReq: BaseIoTReq): IoTRequestBuilder {
        return IoTRequestBuilder()
            .setPath(baseIoTReq.path)
            .setScheme(Scheme.HTTPS)
            .setApiVersion(baseIoTReq.version)
            .setAuthType("iotAuth")
    }

    /**
     * iot请求统一处理
     */
    fun send(
        req: IoTRequest,
        error: (error: Exception?) -> Unit,
        success: (res: IoTResponse?) -> Unit,
        errorResult: ((res: IoTResponse?) -> Unit?)? = null,
    ) {
        ioTClient.send(req, object : IoTCallback {
            override fun onFailure(req: IoTRequest?, exception: Exception?) {
                error.invoke(exception)
                exception?.let {
                    it.printStackTrace()
                    LogPet.e("iotClientSend error = ${Gson().toJson(it)}")
                }
            }

            override fun onResponse(req: IoTRequest?, res: IoTResponse?) {
                LogPet.e(
                    "otaClient path = ${req?.path}},params = ${req?.params}" +
                            " onResponse = code = ${res?.code} ,data = ${Gson().toJson(res?.data)}"
                )
                if (res?.code == 200) {
                    success.invoke(res)
                } else {
                    error.invoke(null)
                    errorResult?.invoke(res)
                    LogPet.e("ali error code :${res?.code ?: 0} ,errorMsg:${res?.localizedMsg}")
                }
            }

        })
    }

}