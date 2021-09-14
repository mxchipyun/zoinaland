package wendu.dsbridge.bean

data class DeviceMsgCountRes(
    val announcement: Int,//分享消息数量。
    val device: Int,//设备消息的数量。
    val share: Int//通知消息的数量。
)