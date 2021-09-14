package com.mxchip.myapplication.ui.pair

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wendu.dsbridge.bean.DeviceSupportRes
import wendu.dsbridge.net.api.iot.IoTRep
import wendu.dsbridge.net.api.iot.IoTRepProvision
import wendu.dsbridge.net.api.iot.IoTRepProvision.Companion.ON_PROVISIONED_RESULT_SUCCESS

class DeviceSupportVM : ViewModel() {

    val supportDeviceData = mutableListOf<DeviceSupportRes>()
    val supportDeviceDataChanged = MutableLiveData<Boolean>()
    val bindResult = MutableLiveData<Boolean>()

    companion object {
        val FEED_P_K = "a1xfUsB4xod"
        val FEED_P_ID = "7861320"
    }

    private val ioTRepProvision = IoTRepProvision()
    private val iotRep = IoTRep()
    fun getSupportDevice() {
        IoTRep().getSupportDevice {
            if (!it.isNullOrEmpty()) {
                supportDeviceData.addAll(it)
            }
        }
    }

    fun pair() {
        ioTRepProvision.startProvision(
            FEED_P_K, FEED_P_ID,
            "AP057", "12345678"
        ) { provisionStatus, userBindRes ->
            if (provisionStatus == ON_PROVISIONED_RESULT_SUCCESS)
                bindResult.postValue(true)
        }
    }

    fun pairZero(context: Context) {
    }

}