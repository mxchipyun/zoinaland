package wendu.dsbridge.bean

//{"accessMethod":0,"productModel":"投食器","gmtModified":1621838357000,"productKey":"a1xfUsB4xod",
// "categoryName":"宠物喂食机","image":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559628487679.png",
// "netType":3,"dataFormat":0,"categoryKey":"petFeeder","nodeType":0,"gmtCreate":1617088541000,
// "scriptId":502055,"domain":"a1xfUsB4xod","name":"鸟语花香宠物投食器","tenantId":"9C339FA1BEED4556ADB134F8306EAFE0",
// "region":"cn-shanghai","categoryId":2308,"status":1}}
data class DeviceSupportRes(
    val accessMethod: Int,
    val categoryKey: String,//产品所属品类的品类标识符。
    val categoryName: String,//产品所属品类的名称。
    val dataFormat: Int,//设备上传的数据格式。0（表示透传/自定义格式）；1（表示使用Alink协议数据格式）。
    val gmtCreate: Long,
    val gmtModified: Long,
    val image: String,
    val productModel: String,
    val name: String,//产品名称。
    val netType: Int,//产品入网类型：0（表示LoRa）；3（表示Wi-Fi）；4（表示ZigBee）；5（表示Bluetooth）；6（表示蜂窝网）；7（表示以太网）；8（表示其他入网方式）。
    val nodeType: Int,//节点类型。0（表示设备）；1（表示网关）。
    val productKey: String,//产品的Key，设备证书信息之一。创建产品时，生活物联网平台为该产品颁发的全局唯一标识。
    val region: String,
    val status: Int,//产品状态。0（表示开发中）；1（表示已发布）。
    val tenantId: String
) {


    override fun equals(other: Any?): Boolean {
        if (other is DeviceSupportRes) {
            return other.productKey == productKey
                    && productModel == other.productModel && categoryKey == other.categoryKey
        }
        return super.equals(other)
    }

}