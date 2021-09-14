package wendu.dsbridge.bean

data class ConfigBean(
    val baseUrl: String,
    val appKey: String,//后台给的appid
    val appSecret: String//后台给的秘钥
)
