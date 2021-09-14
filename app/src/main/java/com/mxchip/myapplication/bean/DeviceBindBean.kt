package com.mxchip.myapplication.bean

import wendu.dsbridge.H5Load.H5LocalCacheUtil



data class DeviceData(
    val `data`: List<DeviceBean>?,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int
)

data class DeviceBean(
    val bindTime: Long,
    val categoryImage: String,
    val categoryKey: String,
    val categoryName: String,
    val deviceName: String,
    val gmtModified: Long,
    val identityAlias: String,
    val identityId: String,
    val iotId: String,
    val isEdgeGateway: Boolean,
    val netType: String,
    val nodeType: String,
    val owned: Int,
    val productImage: String,
    val productKey: String,
    val productModel: String,
    val productName: String,
    val status: Int,
    val thingType: String
) {
    fun deviceNameImp(): String {
        return "名称：$productName"
    }

    fun iotIdImpl(): String {
        return "iotId：$iotId"
    }

    fun productKeyImpl(): String {
        return "pk：$productKey"
    }

    fun getUrl(): String {
        val mainPage = H5LocalCacheUtil.getH5ModuleLocalCache(productKey).mainPage
        var url = "$mainPage?iotId=$iotId" +
                "&productKey=$productKey" +
                "&isOwner=$owned"
        if (!url.startsWith("http")) {
            url = "file://$url"
        }
        return url
    }
}