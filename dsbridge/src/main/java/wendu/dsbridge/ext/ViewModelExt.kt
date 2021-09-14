package wendu.dsbridge.ext

import kotlinx.coroutines.*
import wendu.dsbridge.util.LogPet

fun requestNet(
    needShowLoading: Boolean? = true,
    errorCallback: ((errorMsg: String) -> Unit)? = null,
    apiFunc: suspend () -> Unit
): Job {

    return GlobalScope.launch {
        try {
            if (this.isActive) {
//                if (needShowLoading == true)
//                    showLoading()
                apiFunc.invoke()
//                if (needShowLoading == true)
//                    dismissLoading()
            }
        } catch (e: Exception) {
            /*manage exception*/
            e.printStackTrace()
//            if (needShowLoading == true)
//                dismissLoading()
            errorCallback?.invoke(e.message.toString())
            LogPet.e("request error:${e.message}")
        }
    }
}
