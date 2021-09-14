package wendu.dsbridge.bean


data class BindByDevRes(
    val `data`: List<BindByDevBean>,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class BindByDevBean(
    val bindTime: Long,
    val categoryImage: String,
    val description: String,
    val deviceName: String,
    val gmtModified: Long,
    val identityAlias: String,
    val identityId: String,
    val iotId: String,
    val netType: String,
    val nodeType: String,
    val owned: Int,
    val productImage: String,
    val productKey: String,
    val productModel: String,
    val productName: String,
    val status: Int,
    val thingType: String
){

}