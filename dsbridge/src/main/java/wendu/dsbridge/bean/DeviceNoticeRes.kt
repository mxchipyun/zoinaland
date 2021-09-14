package wendu.dsbridge.bean

data class DeviceNoticeRes(
    val `data`: List<DeviceNoticeBean>,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class DeviceNoticeBean(
    val body: String,
    val deviceType: String,
    val eventId: String,
    val extData: ExtData,
    val gmtCreate: Long,
    val gmtModified: Long,
    val id: Long,
    val iotId: String,
    var isRead: Int,
    val keyId: String,
    val messageId: String,
    val messageType: String,
    val tag: Int,
    val target: String,
    val targetValue: String,
    val title: String,
    val type: String
) {



    var selected = false
    var selecting = false

    override fun equals(other: Any?): Boolean {
        if (other != null && other is DeviceNoticeBean) {
            return other.selected == selected && other.selecting == selecting
        }
        return super.equals(other)
    }
}

data class ExtData(
    val device: Device
)

data class Device(
    val categoryId: Int,
    val icon: String,
    val iotId: String,
    val productKey: String,
    val productName: String
)