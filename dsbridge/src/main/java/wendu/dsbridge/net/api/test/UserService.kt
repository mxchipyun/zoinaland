package wendu.dsbridge.net.api.test

import retrofit2.http.Body
import retrofit2.http.PUT
import wendu.dsbridge.bean.BaseResp
import wendu.dsbridge.bean.LoginRes

/**
 * 用户相关接口
 */
interface UserService {

    @PUT("/app/v1/user/syncIdentityid")
    suspend fun syncIdentityId(@Body identityid: LoginRes): BaseResp<Boolean>?


}