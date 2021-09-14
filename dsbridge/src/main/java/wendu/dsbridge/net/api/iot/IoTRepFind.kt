package wendu.dsbridge.net.api.iot

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr
import com.google.gson.Gson
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.SysUtil
import java.util.*

/**
 * 提供设备查找功能
 */
class IoTRepFind {

    private val iotApi = IoTRep()

    //搜索设备
    fun startDiscovery(
        enumSet: EnumSet<DiscoveryType>? = null,
        callback: (discoveryType: DiscoveryType, data: List<DeviceInfo>) -> Unit
    ) {
        LogPet.e("startDiscovery-- ")
        var type = EnumSet.allOf(DiscoveryType::class.java)
        enumSet?.let {
            type = it
        }
        LocalDeviceMgr.getInstance().startDiscovery(
            SysUtil.getAppContext(), type, null
        ) { mDiscoveryType, list ->
            LogPet.e("${Gson().toJson(mDiscoveryType)} ,${Gson().toJson(list)}")
            callback.invoke(mDiscoveryType, list)
//            if (!list.isNullOrEmpty()) {
//                list.forEach {
//                    val deviceFindRes = DeviceFindRes(
//                        devType = it.devType,
//                        linkType = it.linkType,
//                        mac = it.mac,
//                        protocolVersion = it.protocolVersion,
//                        productId = it.productId
//                    )
//                    iotApi.getPkByPid(it.productId) { pk ->
//                        deviceFindRes.productKey = pk
//                        callback.invoke(deviceFindRes)
//                    }
//                }
//            }
        }
    }

}