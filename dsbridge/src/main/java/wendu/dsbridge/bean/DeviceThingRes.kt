package wendu.dsbridge.bean

//link @https://help.aliyun.com/document_detail/177836.html?spm=5176.11065259.1996646101.searchclickresult.203e24cadslOwl
data class DeviceThingRes(
    val activeTime: String,
    val deviceSecret: String,
    val firmwareVersion: String,
    val gmtCreate: Long,
    val gmtModified: Long,
    val iotId: String,
    val mac: String,
    val name: String,
    val netAddress: String,
    val nickname: String,
    val productKey: String,
    val rbacTenantId: String,
    val sdkVersion: String,
    val sn: String,
    val status: Int,
    val statusLast: Int,
    val thingType: String
) {
    var wifiVersion: String? = ""
}