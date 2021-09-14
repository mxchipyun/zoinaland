package wendu.dsbridge.net.api.test

import wendu.dsbridge.bean.LoginRes
import wendu.dsbridge.net.RetrofitManager

class UserRepository {

    private val userService = RetrofitManager.getService(UserService::class.java)


    //同步identityId
    suspend fun syncIdentityId(loginRes: LoginRes): Boolean? {
        return userService?.syncIdentityId(loginRes)?.convert()
    }



}