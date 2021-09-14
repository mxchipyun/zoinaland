package com.mxchip.myapplication

import com.aliyun.iot.aep.sdk.log.ALog
import com.aliyun.iot.aep.sdk.log.IALogCloud
import wendu.dsbridge.MxApp
import wendu.dsbridge.util.LogPet
import kotlin.properties.Delegates


class App : MxApp() {

    companion object {
        private var instance: App by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initIoTSmart()

        ALog.setALogCloud(object : IALogCloud {
            override fun d(s: String, s1: String) {
                LogPet.Companion.e(s1)
            }

            override fun i(s: String, s1: String) {
                LogPet.Companion.e(s1)
            }

            override fun w(s: String, s1: String) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String, s2: String) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String, e: Exception) {
                LogPet.Companion.e(s1)
            }

            override fun d(s: String, s1: String, b: Boolean) {
                LogPet.Companion.e(s1)
            }

            override fun i(s: String, s1: String, b: Boolean) {
                LogPet.Companion.e(s1)
            }

            override fun w(s: String, s1: String, b: Boolean) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String, b: Boolean) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String, s2: String, b: Boolean) {
                LogPet.Companion.e(s1)
            }

            override fun e(s: String, s1: String, e: Exception, b: Boolean) {
                LogPet.Companion.e(s1 + e.toString())
            }

            override fun setLevel(b: Byte) {}
            override fun configCloudLog(s: String, s1: String, s2: String, s3: String) {}
        })
    }





}



