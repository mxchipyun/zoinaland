package com.mxchip.myapplication.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mxchip.myapplication.bean.DeviceBean
import com.mxchip.myapplication.bean.DeviceData
import wendu.dsbridge.net.api.iot.IoTRep
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.MxUtil
import wendu.dsbridge.util.ToastPet

class MainVM : ViewModel() {
    private val iotApi = IoTRep()
    val deviceList = mutableListOf<DeviceBean>()
    val deviceDataChanged = MutableLiveData<Boolean>()

    //获取已绑定列表
    fun getListBindingByAccount() {
        iotApi.getListByAccount {
            if (it.isNullOrEmpty()) {
                return@getListByAccount
            }
            val dataBindingBean = JsonUtil.fromJson(it, DeviceData::class.java)
            val mData = dataBindingBean.data
            deviceList.clear()
            if (mData != null)
                deviceList.addAll(mData)
            deviceDataChanged.postValue(true)
        }
    }

    //打开面板
    fun getH5Panel(context: Context, bean: DeviceBean) {

        MxUtil.openDevicePanel(context, bean.iotId, bean.productKey, bean.owned){ code, msg->

        }



    }

    fun unbindDevice(bean: DeviceBean) {
        iotApi.unbindAccountAndDev(bean.iotId) {
            ToastPet.showShort("解绑成功")
            getListBindingByAccount()
        }
    }
}