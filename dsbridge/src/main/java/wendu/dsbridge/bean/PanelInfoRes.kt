package wendu.dsbridge.bean

import wendu.dsbridge.bean.panel.H5LocalCache


/**
 * errorCode : 10404-云端没有可用版本
 */
data class PanelInfoRes(
    val account_id: Int,
    val h5_panel_id: Int,
    val md5: String,
    val release_type: String,
    val remark: String,
    val download_url: String,
    val version: String
) {
    var productKey: String? = null

    fun isError(code: Int): Boolean {
        return code == 10404
    }

    fun createH5ModuleCache(): H5LocalCache {
        val h5ModuleLocalCache = H5LocalCache()
        h5ModuleLocalCache.modelName = productKey
        h5ModuleLocalCache.currentVerison = version
        return h5ModuleLocalCache
    }
}
