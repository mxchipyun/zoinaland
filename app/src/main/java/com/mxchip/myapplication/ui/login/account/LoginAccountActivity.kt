package com.mxchip.myapplication.ui.login.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mxchip.myapplication.databinding.ActivityLoginAccountBinding
import com.mxchip.myapplication.ui.main.MainActivity
import wendu.dsbridge.util.LogPet
import wendu.dsbridge.util.MxUtil
import wendu.dsbridge.util.MxUtil.init


class LoginAccountActivity : AppCompatActivity() {

    private val mVM by viewModels<LoginAccountVM>()
    private lateinit var binding: ActivityLoginAccountBinding

    private val defPk = "a1Ss16Q8p3i"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAccountBinding.inflate(layoutInflater).apply {
            this.vm = mVM
            this.lifecycleOwner = this@LoginAccountActivity
            setContentView(root)
        }


        binding.btnLogin.setOnClickListener {
            init(
                "mxChip2021",
                "mxChip20210826n92f4cb86c9",
                "http://47.102.149.115:8443"
            )
            MxUtil.openDevicePanel(
                this, "4xks3VvO2kdeNuprKebs000000",
                mVM.phoneNumber.value ?: defPk, 1
            ) { code: Int, msg: String ->
                LogPet.e("code:$code ,msg:$msg")
            }

            //固件升级界面
//            Router.getInstance().toUrl(this, "https://com.aliyun.iot.ilop/page/ota/list")

        }

        binding.btnLoginRelease.setOnClickListener {
            init(
                "mxChip2021",
                "mxChip20210826n92f4cb86c9",
                "https://panel.zoina.cn:8443"
            )
            MxUtil.openDevicePanel(
                this, "4xks3VvO2kdeNuprKebs000000",
                mVM.phoneNumber.value ?: defPk, 1
            ) { code: Int, msg: String ->
                LogPet.e("code:$code ,msg:$msg")
            }

            //固件升级界面
//            Router.getInstance().toUrl(this, "https://com.aliyun.iot.ilop/page/ota/list")

        }
        initObserver()

    }

    private fun initObserver() {
        mVM.loginResult.observe(this, {
            navigateToHome()
        })
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}