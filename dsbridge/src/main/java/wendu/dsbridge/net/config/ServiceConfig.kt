package wendu.dsbridge.net.config

import com.aliyun.iot.aep.sdk.framework.utils.SpUtil
import wendu.dsbridge.bean.ConfigBean
import wendu.dsbridge.bean.DeviceSupportRes
import wendu.dsbridge.bean.LoginRes
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.MxUtil
import wendu.dsbridge.util.SysUtil

class ServiceConfig {

    companion object {
        const val pageSize = 20


        fun getConfig(): ConfigBean ?{
            return  MxUtil.configBean
        }


        fun getLoginRes(): LoginRes? {
            CacheConfig.token?.let {
                return it
            }
            val json = SpUtil.getString(SysUtil.getAppContext(), SpKey.TOKEN)
            if (json.isNullOrEmpty()) {
                return null
            }
            val bean = JsonUtil.fromJson(json, LoginRes::class.java)
            CacheConfig.token = bean
            return bean
        }

        fun saveLoginRes(loginRes: LoginRes) {
            CacheConfig.token = loginRes
            SpUtil.putString(SysUtil.getAppContext(), SpKey.TOKEN, JsonUtil.toJson(loginRes))
        }

        fun clearToken() {
            CacheConfig.token = null
            SpUtil.putString(SysUtil.getAppContext(), SpKey.TOKEN, null)
        }

    }
}

object CacheConfig {
    var token: LoginRes? = null
    var supportDeviceList = mutableListOf<DeviceSupportRes>()
}