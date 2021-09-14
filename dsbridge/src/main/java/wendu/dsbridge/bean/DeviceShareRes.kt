package wendu.dsbridge.bean


data class DeviceShareRes(
    val `data`: List<DeviceShareBean>,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class DeviceShareBean(
    val batchId: String,
    val categoryImage: String,
    val description: String,
    val deviceName: String,
    val gmtCreate: Long,
    val gmtModified: Long,
    val initiatorAlias: String,
    val isReceiver: Int,//当前用户是否是消息接收者。0（表示当前用户是此消息的发起者）；1（表示当前用户是接收者）。
    val nodeType: String,
    val productImage: String,
    val productName: String,
    val receiverAlias: String,
    val recordId: String,
    val status: Int,//状态 。-1:（表示初始化）；0（表示同意）；1（表示拒绝 ）；2（表示取消）；3（表示过期）；4（表示抢占）；5（表示删除）；6（表示发起者已解绑）；99（表示异常）。
    val targetId: String,
    val targetType: String
) {


}