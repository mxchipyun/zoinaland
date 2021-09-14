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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAccountBinding.inflate(layoutInflater).apply {
            this.vm = mVM
            this.lifecycleOwner = this@LoginAccountActivity
            setContentView(root)
        }


        //http://192.168.19.187:8001/api/v1/h5panel/url/
        //  appId = "b0baae0630f444b0811ea3c2eb212170",
        //                appKey = "32554858"


//        # app唯⼀标识
//        AppKey = "mxChip2021"
//        AppSerect = "mxChip20210826n92f4cb86c9"
        init(
            "mxChip2021",
            "mxChip20210826n92f4cb86c9",
            "http://47.102.149.115"
        )

        binding.btnLogin.setOnClickListener {

            MxUtil.openDevicePanel(
                this, "4xks3VvO2kdeNuprKebs000000",
                "a1DHMl5941F", 1
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