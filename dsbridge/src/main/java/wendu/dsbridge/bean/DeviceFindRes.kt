package wendu.dsbridge.bean

import android.location.Location

data class DeviceFindRes(
    val devType: String? = null,
    val linkType: String? = null,
    val mac: String? = null,
    var productId: String? = null,
    val protocolVersion: String? = null
) {
    var productKey: String? = null//产品key，通过productId调用阿里接口获取
    var productName: String? = null//名称，产品支持设备数据获取
    var image: String? = null
    var softId: String? = null

    var ssid: String? = null//wifi账号名
    var password: String? = null//wifi密码
    var latitude: String? = null//纬度
    var longitude: String? = null//经度
    var iotId: String? = null

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is DeviceFindRes) {
            return false
        }
        return mac == other.mac
                && linkType == other.linkType
                && devType == other.devType
                && productId == other.productId
                && protocolVersion == other.protocolVersion
                && productKey == other.productKey
    }

}