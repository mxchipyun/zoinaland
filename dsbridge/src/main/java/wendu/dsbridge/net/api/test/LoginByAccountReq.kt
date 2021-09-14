package wendu.dsbridge.net.api.test


data class LoginByAccountReq(
    val account: String,
    val password: String,
    val clientid: String = "",//推送的sdk的clientid，没有传空字符串
    val area: String? = null
)
