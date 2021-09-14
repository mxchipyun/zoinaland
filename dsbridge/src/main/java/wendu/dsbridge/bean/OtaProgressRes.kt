package wendu.dsbridge.bean


//link @https://help.aliyun.com/document_detail/188282.html?spm=a2c4g.11186623.6.935.44e7553fhNU4aV
data class OtaProgressRes(
    val currentVersion: String? = null,
    val desc: String? = null,
    val firmwareVersion: String? = null,
    val moduleName: String? = null,
    val needConfirm: Boolean? = null,
    val otaType: String? = null,
    val status: String? = null,
    val step: Int? = null
)