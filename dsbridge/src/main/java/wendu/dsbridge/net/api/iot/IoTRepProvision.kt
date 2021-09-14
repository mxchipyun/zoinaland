package wendu.dsbridge.net.api.iot

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.aliyun.alink.business.devicecenter.api.add.*
import com.aliyun.alink.business.devicecenter.api.config.ProvisionConfigCenter
import com.aliyun.alink.business.devicecenter.api.config.ProvisionConfigParams
import com.aliyun.alink.business.devicecenter.api.discovery.*
import com.aliyun.alink.business.devicecenter.base.DCErrorCode
import com.aliyun.iot.aep.sdk.framework.region.RegionManager
import wendu.dsbridge.bean.UserBindRes
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.SysUtil
import java.util.*


/**
 * @link https://help.aliyun.com/document_detail/130123.htm?spm=a2c4g.11186623.2.6.7a566f9fn6958Z#concept-1680721
 * 提供配网功能
 */
class IoTRepProvision {
    companion object {

        const val SET_DEVICE_INFO = 1
        const val ON_PRE_CHECK = 2
        const val ON_PROVISION_PREPARE = 3
        const val ON_PROVISIONING = 4
        const val ON_PROVISION_STATUS = 5
        const val ON_PROVISIONED_RESULT_SUCCESS = 6
        const val ON_PROVISIONED_RESULT_FAILED = 7
        const val timeout = 3 * 60 //单位秒，目前最短只能设置为60秒
        const val timeoutNum = 2 //超时重连2次
    }

    private var mTimeoutNum = 0

    private val mHandler = Handler(Looper.getMainLooper())

    fun getToken(context: Context, pk: String?, deviceName: String?,
                 successCallbak: (token: String?) -> Unit, failedCallback: (code: String?, msg: String?) -> Unit) {

        /**
         * 第一步：获取绑定token
         */
        val tokenParams = GetTokenParams().apply {
            this.productKey = pk
            this.deviceName = deviceName
            this.interval = 10 * 1000
            this.timeout = 60 * 1000
        }

        LocalDeviceMgr.getInstance().getDeviceToken(context, tokenParams, object : IOnTokenGetListerner {
            override fun onSuccess(result: GetTokenResult?) {
                successCallbak.invoke(result?.token)
            }


            override fun onFail(p0: DCErrorCode?) {
                failedCallback.invoke(p0?.code, p0?.msg)
            }

        })

    }


    fun startProvision(
        pk: String,
        pId: String?,
        wifiName: String?,
        wifiPwd: String?,
        provisionCallback: (provisionStatus: Int, userBindRes: UserBindRes?) -> Unit
    ) {
        if (pId.isNullOrEmpty() || wifiName.isNullOrEmpty() || wifiPwd.isNullOrEmpty()) {
            LogPet.d("pId or wifiName or wifiPwd can't be null")
            return
        }
        try {
            //1 设置device信息
            run setDeviceInfo@{
                // 启用全球配网时，ProvisionConfigParams的设置只需在配网前调用一次即可，无需每次配网前都调用
                val params = ProvisionConfigParams()
                params.enableGlobalCloudToken = true
                ProvisionConfigCenter.getInstance().setProvisionConfiguration(params)
                val deviceInfo = DeviceInfo().apply {
                    productKey = pk // 商家后台注册的productKey，不可为空
                    productId = pId // 产品ID，蓝牙辅助配网时必配
                    linkType = LinkType.ALI_BLE.name
                }
                // 如果当前App需要全球使用，且涉及到切换账号的数据中心，配网SDK可以按照以下设置传递数据中心的信息
                // getStoredShortRegionId接口由IoTSmart所在的SDK提供
                val regionInfo = RegionInfo()
                regionInfo.shortRegionId = RegionManager.getStoredShortRegionId().toInt()
                deviceInfo.regionInfo = regionInfo
                //设置待添加设备的基本信息
                AddDeviceBiz.getInstance().setDevice(deviceInfo)
                provisionCallback.invoke(SET_DEVICE_INFO, null)
            }

            //2 开始设备配网
            AddDeviceBiz.getInstance().startAddDevice(
                SysUtil.getAppContext(),
                object : IAddDeviceListener {
                    override fun onPreCheck(checkSuccess: Boolean, p1: DCErrorCode?) {
                        // 参数检测回调
                        if (checkSuccess) {
                            provisionCallback.invoke(ON_PRE_CHECK, null)
                        }
                        LogPet.e(
                            "onPreCheck checkSuccess=$checkSuccess ,p1 = ${
                                p1?.let {
                                    JsonUtil.toJson(
                                        it
                                    )
                                }
                            }"
                        )
                    }

                    /**
                     * 1:一键配网、蓝牙辅助配网、设备热点配网、二维码配网、
                     * 2:手机热点配网
                     */
                    override fun onProvisionPrepare(prepareType: Int) {
                        provisionCallback.invoke(ON_PROVISION_PREPARE, null)
                        //3 输入账号密码
                        LogPet.e("onProvisionPrepare prepareType=$prepareType")
                        if (prepareType == 1) {
                            // 手机热点配网的时候注意 要先获取ssid，然后再开启热点，否则无法正确获取到ssid
                            AddDeviceBiz.getInstance().toggleProvision(wifiName, wifiPwd, timeout)
                        }
                    }

                    override fun onProvisioning() {
                        // 配网中
                        provisionCallback.invoke(ON_PROVISIONING, null)
                        LogPet.e("onProvisioning----")
                    }

                    override fun onProvisionStatus(provisionStatus: ProvisionStatus?) {

                        provisionCallback.invoke(ON_PROVISION_STATUS, null)
                        LogPet.e(
                            "onProvisionStatus provisionStatus=${
                                provisionStatus?.let {
                                    JsonUtil.toJson(
                                        it
                                    )
                                }
                            }"
                        )
                    }

                    override fun onProvisionedResult(
                        isSuccess: Boolean,
                        deviceInfo: DeviceInfo,
                        error: DCErrorCode?
                    ) {
                        LogPet.e(
                            "onProvisionedResult isSuccess=$isSuccess ,deviceInfo = ${
                                deviceInfo?.let {
                                    JsonUtil.toJson(
                                        it
                                    )
                                }
                            },error=${
                                error?.let {
                                    JsonUtil.toJson(
                                        it
                                    )
                                }
                            }"
                        )
                        LogPet.d("provision isSuccess === $isSuccess ")
                        if (isSuccess) {
                            //第三步，绑定设备到飞燕，获取ioTid
                            IoTRep().userBind(
                                deviceInfo.productKey,
                                deviceInfo.deviceName
                            ) { userBindRes ->
                                bindResult(userBindRes, provisionCallback)
                            }

                            LogPet.d("provision isSuccess userBind ")

                        } else {
                            LogPet.e("provision failed ")
                            mHandler.removeCallbacksAndMessages(null)
                            provisionCallback.invoke(ON_PROVISIONED_RESULT_FAILED, null)
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            provisionCallback.invoke(ON_PROVISIONED_RESULT_FAILED, null)
        }
        mHandler.postDelayed({
            provisionCallback.invoke(ON_PROVISIONED_RESULT_FAILED, null)
        }, timeout * 1000L)

    }

    fun bindResult(
        userBindRes: UserBindRes?,
        provisionCallback: (provisionStatus: Int, userBindRes: UserBindRes?) -> Unit
    ) {
        mHandler.removeCallbacksAndMessages(null)
        if (userBindRes == null) {
            LogPet.e("provision failed userBind userBindRes == null")
            provisionCallback.invoke(ON_PROVISIONED_RESULT_FAILED, null)
            return
        }
        //第四步，调用后台接口
        provisionCallback.invoke(ON_PROVISIONED_RESULT_SUCCESS, userBindRes)
    }

    //停止配网
    fun stopProvision() {
        AddDeviceBiz.getInstance().stopAddDevice()
    }
}