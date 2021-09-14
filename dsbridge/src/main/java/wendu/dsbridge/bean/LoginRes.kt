package wendu.dsbridge.bean

data class LoginRes(
    val token: String?,
    var identityid: String?,
    var phone: String? = null
)
