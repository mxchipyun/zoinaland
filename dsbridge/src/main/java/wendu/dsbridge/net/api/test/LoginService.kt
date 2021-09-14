package wendu.dsbridge.net.api.test

import retrofit2.http.Body
import retrofit2.http.POST
import wendu.dsbridge.bean.BaseResp
import wendu.dsbridge.bean.LoginRes


/**
 * Auth 相关接口
 */
interface LoginService {

    //账号密码登录
    @POST("/app/v1/auth/login")
    suspend fun loginByAccount(@Body req: LoginByAccountReq): BaseResp<LoginRes>?


}