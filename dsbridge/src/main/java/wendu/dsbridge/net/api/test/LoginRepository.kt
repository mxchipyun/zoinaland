package wendu.dsbridge.net.api.test

import wendu.dsbridge.bean.LoginRes
import wendu.dsbridge.net.RetrofitManager

class LoginRepository {
    private val loginService = RetrofitManager.getService(LoginService::class.java)


    //账号密码登录
    suspend fun loginByAccount(req: LoginByAccountReq): LoginRes? {
        return loginService?.loginByAccount(req)?.convert()
    }



}