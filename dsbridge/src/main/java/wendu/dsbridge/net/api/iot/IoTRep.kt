package wendu.dsbridge.net.api.iot


import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import wendu.dsbridge.bean.*
import wendu.dsbridge.net.api.iot.IotApi.allUpdateReq
import wendu.dsbridge.net.api.iot.IotApi.confirmShareReq
import wendu.dsbridge.net.api.iot.IotApi.deleteRecordReq
import wendu.dsbridge.net.api.iot.IotApi.getBindingByAccount
import wendu.dsbridge.net.api.iot.IotApi.getPKByPidReq
import wendu.dsbridge.net.api.iot.IotApi.infoByIotReq
import wendu.dsbridge.net.api.iot.IotApi.listBindingByDevReq
import wendu.dsbridge.net.api.iot.IotApi.otaCancelReq
import wendu.dsbridge.net.api.iot.IotApi.otaConfirmReqOld
import wendu.dsbridge.net.api.iot.IotApi.otaProgressReqOld
import wendu.dsbridge.net.api.iot.IotApi.otaVersion
import wendu.dsbridge.net.api.iot.IotApi.otaVersionOld
import wendu.dsbridge.net.api.iot.IotApi.propertiesReq
import wendu.dsbridge.net.api.iot.IotApi.recordModifyReq
import wendu.dsbridge.net.api.iot.IotApi.recordQueryReq
import wendu.dsbridge.net.api.iot.IotApi.shareDevicesAndScenesReq
import wendu.dsbridge.net.api.iot.IotApi.shareNoticeList
import wendu.dsbridge.net.api.iot.IotApi.supportDeviceReq
import wendu.dsbridge.net.api.iot.IotApi.unbindAccountAndDevReq
import wendu.dsbridge.net.api.iot.IotApi.unbindByManagerReq
import wendu.dsbridge.net.api.iot.IotApi.userBindByTokenReq
import wendu.dsbridge.net.api.iot.IotApi.userBindReq
import wendu.dsbridge.net.config.CacheConfig
import wendu.dsbridge.net.config.ServiceConfig.Companion.pageSize
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.LogPet

class IoTRep {

    /**
     * @see supportDeviceReq
     */
    fun getSupportDevice(callback: (data: List<DeviceSupportRes>?) -> Unit) {
        val mData = CacheConfig.supportDeviceList
        if (!mData.isNullOrEmpty()) {
            LogPet.e("getSupportDevice CacheConfig = $mData")
            callback.invoke(mData)
            return
        }
        IotApi.send(IotApi.createIoTReq(supportDeviceReq), {
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                val jsonStr = (data as JSONArray).toString()
                val supportData = JsonUtil.fromJson(
                    jsonStr,
                    object : TypeToken<List<DeviceSupportRes>>() {}.type
                ) as List<DeviceSupportRes>
                LogPet.e("getSupportDevice onResponse = $jsonStr")
                val releaseData = supportData.filter {
                    !it.productModel.contains(
                        "demo",
                        true
                    )
                }
                CacheConfig.supportDeviceList.addAll(releaseData)
                callback.invoke(releaseData)
            }
        })
    }

    fun getListByAccount(callback: (result: String?) -> Unit) {
        val req = IotApi.createIoTReq(getBindingByAccount)
        req.params["pageNo"] = 1
        req.params["pageSize"] = 100
        IotApi.send(req, {
            LogPet.e("$it")
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                val jsonStr = (data).toString()
                LogPet.e(jsonStr)
                callback.invoke(jsonStr)
            }
        })
    }

    /**
     * @see getPKByPidReq
     */
    fun getPkByPid(productId: String, callback: (pk: String) -> Unit) {
        val req = IotApi.createIoTReq(getPKByPidReq)
        req.params["productId"] = productId
        IotApi.send(req, {

        }, {
            it?.data?.let { data ->
                if (data is JSONObject) {
                    val pk = data["productKey"].toString()
                    callback.invoke(pk)
                }
            }
        })
    }

    /**
     * @see IotApi.getProductInfoByPkReq
     */
    fun getProductInfoByPk(productKey: String?, callback: (pId: String?) -> Unit) {
        if (productKey.isNullOrEmpty()) {
            return
        }
        val req = IotApi.createIoTReq(IotApi.getProductInfoByPkReq)
        req.params["productkey"] = productKey
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                if (data is JSONObject) {
                    val pId = data["productId"].toString()
                    callback.invoke(pId)
                }
            }
        })
    }

    /**
     * @see userBindReq
     */
    fun userBind(
        productKey: String,
        deviceName: String,
        callback: (res: UserBindRes?) -> Unit
    ) {
        val req = IotApi.createIoTReq(userBindReq)
        req.params["productKey"] = productKey
        req.params["deviceName"] = deviceName
//        req.params["token"] = token
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                val userBindRes = JsonUtil.fromJson(data.toString(), UserBindRes::class.java)
                callback.invoke(userBindRes)
            }
        })

    }

    fun userBindByToken(
        productKey: String,
        deviceName: String,
        token: String? = null,
        callback: (res: UserBindRes?) -> Unit,
        error: ((iotResponse: IoTResponse?) -> Unit)? = null
    ) {
        val req = IotApi.createIoTReq(userBindByTokenReq)
        req.params["productKey"] = productKey
        req.params["deviceName"] = deviceName
        req.params["token"] = token
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                val userBindRes = JsonUtil.fromJson(data.toString(), UserBindRes::class.java)
                callback.invoke(userBindRes)
            }
        }, {
            error?.invoke(it)
        })

    }

    /**
     * @see infoByIotReq
     */
    fun getInfoByIot(iotId: String, callback: (res: DeviceThingRes?) -> Unit) {
        val req = IotApi.createIoTReq(infoByIotReq)
        req.params["iotId"] = iotId
        IotApi.send(req, {
            callback.invoke(null)
        }, { res ->
            val deviceThingRes =
                JsonUtil.fromJson(res?.data.toString(), DeviceThingRes::class.java)
            getProperties(iotId) { jsonObj ->
                try {
                    deviceThingRes.wifiVersion =
                        jsonObj?.getJSONObject("WiFI_RSSI")?.getString("value")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    callback.invoke(deviceThingRes)
                }
            }
        })
    }

    /**
     * @see propertiesReq
     */
    fun getProperties(iotId: String, callback: (res: JSONObject?) -> Unit) {
        val req = IotApi.createIoTReq(propertiesReq)
        req.params["iotId"] = iotId
        IotApi.send(req, {
            callback.invoke(null)
        }, { res ->
            callback.invoke(res?.data as JSONObject?)
        })
    }

    /**
     * ??????????????????????????????
     * @see shareDevicesAndScenesReq
     */
    fun shareDevicesAndScenes(
        iotIds: List<String>? = null,//??????ID???????????????ID??????????????????????????????????????????ID??????????????????????????????iotIdList???sceneIdList?????????????????????
        accountAttr: String? = null,//????????????????????????????????????MOBILE????????????????????????EMAIL??????????????????
        accountAttrType: String? = null,//???????????????????????????????????????????????????????????????????????????????????????"MOBILE"????????????"EMAIL"???
        mobileLocationCode: String? = null,//????????????????????????
        targetIdentityId: String? = null,//?????????????????????ID???
        callback: (success: Boolean) -> Unit
    ) {
        val req = IotApi.createIoTReq(shareDevicesAndScenesReq)
        req.params["iotIdList"] = iotIds
        req.params["targetIdentityId"] = targetIdentityId
        req.params["accountAttr"] = accountAttr
        req.params["accountAttrType"] = "MOBILE"
        req.params["mobileLocationCode"] = "86"
//        req.params["autoAccept"] = false//??????????????????????????????????????????????????????????????????????????????
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ????????????????????????
     * @see otaVersionOld
     */
    fun getOtaVersion(iotId: String?, callback: (success: Boolean) -> Unit) {
        if (iotId.isNullOrEmpty()) {
            callback.invoke(false)
            return
        }
        val req = IotApi.createIoTReq(otaVersionOld)
        req.params["iotId"] = iotId
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    fun getOtaInfo(iotId: String?, callback: (result: String?) -> Unit) {
        if (iotId.isNullOrEmpty()) {
            callback.invoke(null)
            return
        }
        val req = IotApi.createIoTReq(otaVersion)
        req.params["iotId"] = iotId
        req.params["moduleName"] = "mcu"
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { data ->
                if (data is JSONObject) {
                    try {
                        val mcuVersion = data["currentVersion"].toString()
                        callback.invoke(mcuVersion)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.invoke(null)
                    }

                }
            }
        })
    }

    /**
     * ????????????
     * @see otaConfirmReqOld
     */
    fun otaConfirm(iotIds: List<String>, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(otaConfirmReqOld)
        req.params["iotIds"] = iotIds
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ??????????????????
     * @see otaProgressReqOld
     */
    fun otaProgress(iotId: String, version: String, callback: (bean: OtaProgressRes?) -> Unit) {
        val req = IotApi.createIoTReq(otaProgressReqOld)
        req.params["iotId"] = iotId
        req.params["version"] = version
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            val bean = JsonUtil.fromJson(it?.data.toString(), OtaProgressRes::class.java)
            callback.invoke(bean)
        })
    }

    /**
     * ????????????
     * @see otaCancelReq
     */
    fun otaCancel(iotId: String, firmwareVersion: String, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(otaCancelReq)
        req.params["iotId"] = iotId
        req.params["firmwareVersion"] = firmwareVersion
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ??????????????????????????????
     * @see recordQueryReq
     */
    fun getNoticeDeviceMessage(pageNum: Int, callback: (bean: DeviceNoticeRes?) -> Unit) {
        val req = IotApi.createIoTReq(recordQueryReq)
//        req.params["messageType"] = "device"
//        req.params["type"] = "MESSAGE"
//        req.params["sortType"] = 0
//        req.params["nextToken"] = 10
//        req.params["maxResults"] = pageSize
//        val curTime = System.currentTimeMillis()
//        req.params["startCreateTime"] = curTime - 7 * 24 * 60 * 60 * 1000
//        req.params["endCreateTime"] = curTime
        req.params["requestDTO"] = mapOf(
            "messageType" to "device",
            "type" to "MESSAGE",
            "sortType" to 0,
            "nextToken" to pageNum,
            "maxResults" to pageSize
        )
        IotApi.send(req, {
            callback(null)
        }, {
            it?.data?.let { jsonObjRes ->
                callback(JsonUtil.fromJson(jsonObjRes.toString(), DeviceNoticeRes::class.java))
            }
        })
    }

    /**
     * ????????????????????????
     * @see shareNoticeList
     */
    fun getShareNoticeList(pageNo: Int, callback: (bean: DeviceShareRes?) -> Unit) {
        val req = IotApi.createIoTReq(shareNoticeList)
        req.params["pageNo"] = pageNo
        req.params["pageSize"] = pageSize
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { jsonObjRes ->
                callback.invoke(
                    JsonUtil.fromJson(
                        jsonObjRes.toString(),
                        DeviceShareRes::class.java
                    )
                )
            }
        })
    }

    fun clearShareNotice(callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.clearShareNotice)
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ?????????????????????????????????
     * #agree 0-?????????1-??????
     * @see confirmShareReq
     */
    fun confirmShare(recordIds: List<String>, agree: Int, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(confirmShareReq)
        req.params["recordIdList"] = recordIds
        req.params["agree"] = agree
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * @see unbindByManagerReq ?????????????????????
     */
    fun unbindByManager(
        targetIdentityId: String,
        iotIdList: List<String>,
        callback: (success: Boolean) -> Unit
    ) {
        val req = IotApi.createIoTReq(unbindByManagerReq)
        req.params["targetIdentityId"] = targetIdentityId
        req.params["iotIdList"] = iotIdList
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * @see unbindAccountAndDevReq ?????????????????????
     */
    fun unbindAccountAndDev(
        iotId: String,
        callback: (success: Boolean) -> Unit
    ) {
        synchronized(this) {
            val req = IotApi.createIoTReq(unbindAccountAndDevReq)
            req.params["iotId"] = iotId
            IotApi.send(req, {
                callback.invoke(false)
            }, {
                callback.invoke(true)
            })
        }
    }

    /**
     * @see listBindingByDevReq ??????????????????????????????
     */
    fun listBindByDev(pageNo: Int, iotId: String, callback: (bean: BindByDevRes?) -> Unit) {
        val req = IotApi.createIoTReq(listBindingByDevReq)
        req.params["iotId"] = iotId
        req.params["pageNo"] = pageNo
        req.params["pageSize"] = pageSize
        req.params["owned"] = 0
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { jsonObj ->
                callback.invoke(JsonUtil.fromJson(jsonObj.toString(), BindByDevRes::class.java))
            }
        })
    }

    /**
     * @see deleteRecordReq ???????????????????????????
     */
    fun deleteRecord(
        ids: List<Long>,
        callback: (success: Boolean) -> Unit
    ) {
        val req = IotApi.createIoTReq(deleteRecordReq)
        req.params["requestDTO"] = mapOf(
            "type" to "MESSAGE",
            "ids" to ids
        )
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ??????????????????????????????????????????
     */
    fun allRead(callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(allUpdateReq)
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ?????????????????????????????????
     */
    fun recordModify(keyIds: List<String>, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(recordModifyReq)
        req.params["requestDTO"] = mapOf(
            "type" to "MESSAGE",
            "isRead" to 1,
            "keyIds" to keyIds
        )
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    /**
     * ??????????????????????????????
     * #iotId ??????ID??????????????????????????????????????????ID??????????????????????????????
     * #noticeMode ???????????????????????????NONE???????????????????????????MESSAGE??????????????????????????????????????????MESSAGE_AND_NOTICE??????????????????????????????
     */
    fun noticeSet(iotId: String, noticeMode: String, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.noticeSetReq)
        req.params["iotId"] = iotId
        req.params["noticeMode"] = noticeMode
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })

    }

    fun noticeGet(iotId: String, callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.noticeGetReq)
        req.params["iotId"] = iotId
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(it?.data != "NONE")
        })

    }

    //?????????????????????????????????
    fun deviceMsgCount(callback: (bean: DeviceMsgCountRes?) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.deviceMsgCountReq)
        req.params["requestDTO"] = mapOf(
            "type" to "NOTICE"
        )
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            it?.data?.let { jsonObj ->
                callback.invoke(
                    JsonUtil.fromJson(
                        jsonObj.toString(),
                        DeviceMsgCountRes::class.java
                    )
                )
            }
        })
    }

    //??????code??????????????????
    fun taoBaoBind(authCode: String?, callback: (success: Boolean) -> Unit) {
        val builder = IotApi.createIoTBuilder(IotApi.taoBaoBindReq)
        val jsonObject = com.alibaba.fastjson.JSONObject()
        authCode?.let {
            jsonObject.put("authCode", it)
        }
        builder.setParams(jsonObject.innerMap)
        IotApi.send(builder.build(), {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    //????????????????????????
    fun taoBaoUnBind(callback: (success: Boolean) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.taoBaoUnBindReq)
        req.params["accountType"] = "TAOBAO"
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

    //???????????????????????????
    fun taoBaoHadBind(callback: (bean: TaoBaoHadBindRes?) -> Unit) {
        val req = IotApi.createIoTReq(IotApi.taoBaoHadBindReq)
        req.params["accountType"] = "TAOBAO"
        IotApi.send(req, {
            callback.invoke(null)
        }, {
            var mBean: TaoBaoHadBindRes? = null
            it?.data?.let { mData ->
                mBean = JsonUtil.fromJson(mData.toString(), TaoBaoHadBindRes::class.java)
            }
            callback.invoke(mBean)
        })
    }

    //?????????????????????????????????????????????????????????????????????????????????app???app?????????????????????
    fun gatewayPermit(
        iotId: String, productKey: String,
        time: Int, callback: (success: Boolean) -> Unit
    ) {
        val req = IotApi.createIoTReq(IotApi.gatewayPermit)
        req.params["iotId"] = iotId
        req.params["productKey"] = productKey
        req.params["time"] = time
        IotApi.send(req, {
            callback.invoke(false)
        }, {
            callback.invoke(true)
        })
    }

}