package com.alpha.selfemployment.Startup.di.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


enum class AuthState {
    Login , Register , Verify
}


class CredentialViewModel() : ViewModel(){



    private var _authState = MutableStateFlow<AuthState>(AuthState.Login)
    var authStateHandler : StateFlow<AuthState> = _authState.asStateFlow()


    private var _authStateFlow = MutableStateFlow(false)
    var authStateFlow : StateFlow<Boolean> = _authStateFlow.asStateFlow()

    fun set_AuthStateFlow(value: Boolean){
        _authStateFlow.update { value }
    }

    fun clear_AuthStateFlow(){
        _authStateFlow.update { false }
    }

    fun change_AuthState(state : AuthState){
//        clearName()
//        clearMobileNumber()
        clearOtp()
        clearOtpError()
        _authState.update { state }
    }

    fun clear_AuthState(){
        clearName()
        clearMobileNumber()
        clearOtp()
        clearOtpError()
        _authState.update { AuthState.Register }
    }


    var name = MutableStateFlow("")

    fun addName(value: String) {
        name.value = value
    }

    fun clearName(){
        name.value = ""
    }

    var mobileNumber = MutableStateFlow("")

    fun addMobileNumber(value: String){
        mobileNumber.value = value
    }

    fun clearMobileNumber(){
        mobileNumber.value = ""
    }

    var otp = MutableStateFlow("")

    fun addOtp (value : String){
        otp.value = value
    }

    fun clearOtp(){
        otp.value = ""
    }

    var otpError = MutableStateFlow(false)

    fun addOtpError(value : Boolean){
        otpError.value = value
    }

    fun clearOtpError(){
        otpError.value = false
    }

}