package com.mxchip.myapplication.ui.pair

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder
import com.mxchip.myapplication.databinding.ActivitySupportDeviceBinding
import java.util.*

class DeviceSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportDeviceBinding
    private val vm by viewModels<DeviceSupportVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDeviceBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
        binding.tvDeviceFeed.setOnClickListener {
            vm.pair()
        }
        binding.tvDeviceZero.setOnClickListener {
            vm.pairZero(this)
        }
        vm.bindResult.observe(this, {
            finish()
        })
        getSupportDeviceListFromSever()
    }

    /**
     * 获取支持添加的设备列表
     */
    fun getSupportDeviceListFromSever() {
        val maps: MutableMap<String, Any> = HashMap()
        //        IoTRequestBuilder builder = new IoTRequestBuilder()
//                .setPath("/thing/productInfo/getByAppKey")
//                .setApiVersion("1.1.1")
//                .setAuthType("iotAuth")
//                .setParams(maps);
        maps["pageNo"] = 1
        maps["pageSize"] = 100
        maps["productName"] = "太川"
        val builder = IoTRequestBuilder()
            .setPath("/home/app/product/query")
            .setApiVersion("1.0.0")
            .setAuthType("iotAuth")
            .setParams(maps)
        val request = builder.build()
        val ioTAPIClient = IoTAPIClientFactory().client
        ioTAPIClient.send(request, object : IoTCallback {
            override fun onFailure(ioTRequest: IoTRequest, e: Exception) {

            }

            override fun onResponse(ioTRequest: IoTRequest, ioTResponse: IoTResponse) {

            }
        })
    }

}