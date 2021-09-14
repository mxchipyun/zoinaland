package wendu.dsbridge.net.config

import com.aliyun.iot.aep.sdk.framework.utils.SpUtil
import wendu.dsbridge.util.SysUtil

object SpKey {
    const val TOKEN = "token"
    const val HAD_START = "hadStart"
    const val MOB_CLIENT_ID = "mobClientId"
    const val H5_VERSION = "h5Version"


}

object SpValue {
    private var mobClientId = ""

    fun getMobClientId(): String {
        if (mobClientId.isEmpty()) {
            mobClientId = SpUtil.getString(SysUtil.getAppContext(), SpKey.MOB_CLIENT_ID)
        }
        return mobClientId
    }

}