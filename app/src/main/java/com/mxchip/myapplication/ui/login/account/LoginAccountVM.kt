package com.mxchip.myapplication.ui.login.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wendu.dsbridge.net.api.test.LoginRepository

class LoginAccountVM : ViewModel() {

    val phoneNumber = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginResult = MutableLiveData<Boolean>()

    private val loginRepository = LoginRepository()


}