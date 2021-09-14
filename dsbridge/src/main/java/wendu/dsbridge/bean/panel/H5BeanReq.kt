package wendu.dsbridge.bean.panel

data class H5BeanReq(
    val appid: String,
    val nonce: String,
    val product_key: String,
    val sign: String,
    val ts: String,
    val version: String? = null
)