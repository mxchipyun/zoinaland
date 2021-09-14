package wendu.dsbridge.net.api.iot

import android.os.Handler
import android.os.Looper
import com.aliyun.alink.linksdk.channel.core.base.AError
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData
import com.aliyun.iot.aep.sdk.framework.AApplication
import wendu.dsbridge.net.api.test.UserRepository
import wendu.dsbridge.net.config.ServiceConfig
import wendu.dsbridge.util.JsonUtil
import wendu.dsbridge.util.LogPet

/**
 * 提供阿里登录功能
 */
class IoTRepLogin {

    private var handler: Handler = Handler(Looper.getMainLooper())


    //刷新用户认证数据、绑定到淘宝账号
    fun asyncRefreshIoTCredential(
        onSuccessListener: (ioTCredentialData: IoTCredentialData) -> Unit,
        onErrorListener: () -> Unit
    ) {
        //刷新用户认证数据
        IoTCredentialManageImpl.getInstance(AApplication.getInstance())
            .asyncRefreshIoTCredential(object : IoTCredentialListener {
                override fun onRefreshIoTCredentialSuccess(ioTCredentialData: IoTCredentialData) {
                    LogPet.d("onRefreshIoTCredentialSuccess = ${JsonUtil.toJson(ioTCredentialData)}")
                    //绑定到淘宝账号
                    handler.postDelayed({
                        MobileChannel.getInstance().bindAccount(ioTCredentialData.iotToken, object :
                            IMobileRequestListener {
                            override fun onSuccess(result: String?) {
                                LogPet.d("bindAccount onSuccess:$result")
                                onSuccessListener.invoke(ioTCredentialData)
                            }

                            override fun onFailure(error: AError?) {
                                onErrorListener.invoke()
                                LogPet.e("bindAccount onFailure:" + error?.let { JsonUtil.toJson(it) })
                            }

                        })
                    }, 500)
                }

                override fun onRefreshIoTCredentialFailed(error: IoTCredentialManageError) {
                    LogPet.e("onRefreshIoTCredentialFailed：" + JsonUtil.toJson(error))
                    onErrorListener.invoke()
                }
            })
    }

//
//    fun bindPush() {
//        val clientId = PushServiceFactory.getCloudPushService().deviceId
//    }
//
//    fun loginByAli(loginRes: LoginRes, mCallback: IoTLoginCallback) {
//        LogPet.e("loginByAli")
//        LoginBusiness.authCodeLogin(loginRes.token, object : ILoginCallback {
//            override fun onLoginSuccess() {
//                //获取用户认证信息
//                asyncRefreshIoTCredential(
//                    onErrorListener = {
//                        mCallback.onFailed()
//                    }, onSuccessListener = { iotCredentialData ->
//                        // 上传阿里的iotIdentity以做分享使用
//                        loginRes.identityid = iotCredentialData.identity
//                        ServiceConfig.saveLoginRes(loginRes)
//                        requestNet(false) {
//                            UserRepository().syncIdentityId(loginRes).let {
//                                handler.removeCallbacksAndMessages(null)
//                                LogPet.d("loginByAli onLoginSuccess : ${JsonUtil.toJson(loginRes)}")
//                                mCallback.onSuccess()
//                            }
//                        }
//                    })
//            }
//
//            override fun onLoginFailed(code: Int, msg: String?) {
//                handler.removeCallbacksAndMessages(null)
//                //销毁阿里token信息，防止出现token错乱导致阿里登录失效
//                IoTCredentialManageImpl.getInstance(AApplication.getInstance()).clearIoTTokenInfo()
//                LogPet.e("LoginBusiness.authCodeLogin onLoginFailed code=$code ,msg=$msg")
//                mCallback.onFailed()
//            }
//        })
//
//        handler.postDelayed({
//            mCallback.onFailed()
//            ToastPet.showShort("请求超时")
//        }, 6000)
//
//    }

    //更新token，原步骤：上传阿里iotIdentity至后台
    suspend fun dealLoginRes() {
        ServiceConfig.getLoginRes()?.let { loginRes ->
            if (loginRes.identityid.isNullOrEmpty()) {
                val iotIdentity =
                    IoTCredentialManageImpl.getInstance(AApplication.getInstance()).ioTIdentity
                if (!iotIdentity.isNullOrEmpty()) {
                    // 上传阿里的iotIdentity以做分享使用
                    loginRes.identityid = iotIdentity
                    UserRepository().syncIdentityId(loginRes).let {
                        LogPet.e("syncIdentityIdSuccess = $it")
                        ServiceConfig.saveLoginRes(loginRes)
                    }
                }
            }
        }
    }

    fun clearAli() {
        //销毁阿里token信息，防止出现token错乱导致阿里登录失效
        IoTCredentialManageImpl.getInstance(AApplication.getInstance()).clearIoTTokenInfo()
    }
}


interface IoTLoginCallback {

    fun onSuccess()

    fun onFailed()

}