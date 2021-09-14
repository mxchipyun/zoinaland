package wendu.dsbridge.bean

import wendu.dsbridge.util.LogPet

data class BaseResp<T>(
    var code: Int = 0,
    var message: String = "",
    var `data`: T?
) {

    @Suppress("UNCHECKED_CAST")
    fun convert(): T? {
        if (code == 0) {
            return data
        } else {
            LogPet.e("error:code:$code ,message = $message")
        }
        return null
    }

}