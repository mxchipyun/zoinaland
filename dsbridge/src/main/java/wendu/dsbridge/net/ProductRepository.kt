package wendu.dsbridge.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wendu.dsbridge.bean.BaseResp
import wendu.dsbridge.ext.requestNet
import wendu.dsbridge.net.api.ProductService

class ProductRepository {

    private val productService = RetrofitManager.getService(ProductService::class.java)


    fun updateRuleButtonName(
        data: Map<String, @JvmSuppressWildcards Any>,
        callback: FetchCallback
    ) {
        requestNet(false) {
            val mData = productService?.updateRuleButtonName(data)
            GlobalScope.launch(Dispatchers.Main) {
                callback.onSuccess(mData)
            }
        }
    }

    fun fetchOwnServerGET(
        url: String,
        data: Map<String, @JvmSuppressWildcards Any>,
        callback: FetchCallback
    ) {
        requestNet(false) {
            val mData = productService?.fetchOwnServerGET(url, data)
            GlobalScope.launch(Dispatchers.Main) {
                callback.onSuccess(mData)
            }
        }
    }

    fun fetchOwnServerPOST(
        url: String,
        data: Map<String, @JvmSuppressWildcards Any>,
        callback: FetchCallback
    ) {
        requestNet(false) {
            val mData = productService?.fetchOwnServerPOST(url, data)
            GlobalScope.launch(Dispatchers.Main) {
                callback.onSuccess(mData)
            }
        }
    }

    fun fetchOwnServerPUT(
        url: String,
        data: Map<String, @JvmSuppressWildcards Any>,
        callback: FetchCallback
    ) {
        requestNet(false) {
            val mData = productService?.fetchOwnServerPUT(url, data)
            GlobalScope.launch(Dispatchers.Main) {
                callback.onSuccess(mData)
            }
        }
    }

    fun fetchOwnServerDELETE(
        url: String,
        data: Map<String, @JvmSuppressWildcards Any>,
        callback: FetchCallback
    ) {
        requestNet(false) {
            val mData = productService?.fetchOwnServerDELETE(url, data)
            GlobalScope.launch(Dispatchers.Main) {
                callback.onSuccess(mData)
            }
        }
    }


}

interface FetchCallback {
    fun onSuccess(obj: BaseResp<Any>?)
}