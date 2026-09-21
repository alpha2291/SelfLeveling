package com.alpha.selfemployment.Startup.di.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Startup.domain.model.OtpResponse
import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import com.alpha.selfemployment.Startup.domain.repository.AuthRepository
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AuthApiViewModel(
    private val repo : AuthRepository,
    private val appPrefs : AppPreferences
) : ViewModel() {


    private val _registerState = MutableStateFlow<UiState<RegisterResponse>>(UiState.Idle)
    val registerState = _registerState.asStateFlow()


    fun register(
        name: String,
        phone_num_cc: String,
        phone_num: String,
        device_id: String,
        device_type: String,
        device_token: String,
    ) {
        viewModelScope.launch {

            val jsonObject = JSONObject().apply {
                put("name", name)
                put("phone_num_cc", phone_num_cc)
                put("phone_num", phone_num)
                put("device_id", device_id)
                put("device_type", device_type)
                put("device_token", device_token)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.register(requestBody).collect { result ->
                    when (result) {


                        is ResultHandler.Success -> {

                            appPrefs.saveUserId(result.data.data.first().user_id ?: 0)
                            //appPrefs.savePhoneNumber(result.data.data.first().phone_num ?: "")
                            appPrefs.saveRealName(result.data.data.first().name ?:"")


                            toast(result.data.data.first().otp ?:"")
                            _registerState.value = UiState.Success(result.data)
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            _registerState.value = UiState.Error(result.message)
                        }

                        else -> Unit
                    }
                }
            }
            catch (e: Exception) {
                toast(message = e.message?: "")
                _registerState.value = UiState.Error(e.message?: "")
            }
        }
    }


    private val _verifyState = MutableStateFlow<UiState<OtpResponse>>(UiState.Idle)
    val verifyState = _verifyState.asStateFlow()

    fun verify(
        user_id: Int,
        phone_num: String,
        new_phone_num: String,
        whatsapp_num: String,
        email: String,
        otp: String,
        device_id: String,
        device_type: String,
        device_token: String,
    ) {
        viewModelScope.launch {

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("phone_num", phone_num)
                put("new_phone_num", new_phone_num)
                put("whatsapp_num", whatsapp_num)
                put("email", email)
                put("otp", otp)
                put("device_id", device_id)
                put("device_type", device_type)
                put("device_token", device_token)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.verify(requestBody).collect { result ->
                    when (result) {


                        is ResultHandler.Success -> {
                            _verifyState.value = UiState.Success(result.data)

                            appPrefs.saveUserId(result.data.data.first()?.user_id ?: 0)
                            appPrefs.saveRealName(result.data.data.first()?.name ?: "")
                            appPrefs.save_UserName(result.data.data.first()?.username ?:"")
                            appPrefs.savePhoneNumber(result.data.data.first()?.phone_num ?:"")

                            appPrefs.saveUserToken(result.data.data.first()?.token ?:"")
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            _verifyState.value = UiState.Error(result.message)
                        }

                        else -> Unit
                    }
                }
            }
            catch (e: Exception) {
                toast(message = e.message?: "")
                _verifyState.value = UiState.Error(e.message?: "")
            }
        }
    }



    private val _loginState = MutableStateFlow<UiState<RegisterResponse>>(UiState.Idle)
    val loginState = _loginState.asStateFlow()



    fun login(
        phone_num: String,
        phone_num_cc: String,
        device_id: String,
        device_type: String,
        device_token: String,
    ) {
        viewModelScope.launch {

            val jsonObject = JSONObject().apply {
                put("phone_num", phone_num)
                put("phone_num_cc", phone_num_cc)
                put("device_id", device_id)
                put("device_type", device_type)
                put("device_token", device_token)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.login(requestBody).collect { result ->
                    when (result) {


                        is ResultHandler.Success -> {


                            appPrefs.saveUserId(result.data.data.first().user_id ?: 0)
                            appPrefs.savePhoneNumber(result.data.data.first().phone_num ?: "")
                            appPrefs.saveRealName(result.data.data.first().name ?: "")


                            toast(result.data.data.first().otp ?: "")

                            _loginState.value = UiState.Success(result.data)

                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            _loginState.value = UiState.Error(result.message)
                        }

                        else -> Unit
                    }
                }
            }
            catch (e: Exception) {
                toast(message = e.message?: "")
                _loginState.value = UiState.Error(e.message?: "")
            }
        }
    }
}