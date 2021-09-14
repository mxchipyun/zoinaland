package com.mxchip.myapplication.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl
import com.mxchip.myapplication.databinding.ActivityMainTestBinding
import com.mxchip.myapplication.ui.login.account.LoginAccountActivity
import com.mxchip.myapplication.ui.pair.DeviceSupportActivity
import wendu.dsbridge.net.config.ServiceConfig
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.MxUtil
import wendu.dsbridge.util.RouterListener
import wendu.dsbridge.util.SysUtil

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION: Int = 1
    private val vm: MainVM by viewModels()
    private lateinit var binding: ActivityMainTestBinding
    private var adapter: MainAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTestBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        initRecycleView()
        initObserver()

        binding.srl.setOnRefreshListener {
            binding.srl.isRefreshing = false
            vm.getListBindingByAccount()
        }
        binding.tvAddDevice.setOnClickListener {
            startActivity(Intent(this, DeviceSupportActivity::class.java))
        }
        addAliTokenListener()

        requestPermission(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        MxUtil.registerRouterListener(object : RouterListener {

            override fun navigate(path: String?, params: String?) {
                LogPet.e("path = $path")

            }

        })
    }

    fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)
    }

    override fun onResume() {
        super.onResume()
        vm.getListBindingByAccount()
    }

    private fun initRecycleView() {
        adapter = MainAdapter(vm.deviceList, {
            vm.getH5Panel(this, it)
        }) {
            vm.unbindDevice(it)
        }
        binding.rvDevice.adapter = adapter
    }

    private fun initObserver() {
        vm.deviceDataChanged.observe(this, {
            adapter?.notifyDataSetChanged()
        })
    }


    private fun addAliTokenListener() {
        IoTCredentialManageImpl.getInstance(SysUtil.getAppContext())
            .setIotTokenInvalidListener { //ali token失效
                LogPet.e("ali token 失效")
                ServiceConfig.clearToken()
                startActivity(Intent(this, LoginAccountActivity::class.java))
            }
    }

}