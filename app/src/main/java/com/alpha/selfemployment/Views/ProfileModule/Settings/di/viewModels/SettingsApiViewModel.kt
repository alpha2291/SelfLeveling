package com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Startup.domain.model.RegisterResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.LogoutResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.MyInterestResponse
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.MyInterestResponseData
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.NotificationSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.NotificationSettingsData
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.UserNameUpdate
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.repository.SettingsRepository
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.AllInterestsUiState
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SettingsApiViewModel(
    private val repo : SettingsRepository,
    private val appPrefs : AppPreferences,
    private val sharedRepository: SharedRepository
) : ViewModel() {


    fun logout(
        user_id: Int,
        resultHandler: (ResultHandler<ResultHandler.Success<LogoutResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.logout(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }


    fun activate_Deactivate_Account(
        user_id: Int,
        status: String,
        account_delete_type: String,
        account_delete_sentence: String,
        resultHandler: (ResultHandler<ResultHandler.Success<LogoutResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("status", status)
                put("account_delete_type", account_delete_type)
                put("account_delete_sentence", account_delete_sentence)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.activate_Deactivate_Account(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    fun change_Number(
        user_id: Int,
        new_phone_num: String,
        resultHandler: (ResultHandler<ResultHandler.Success<RegisterResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("new_phone_num", new_phone_num)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.change_Number(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    fun update_Profile(
        user_id: Int,
        name: String,
        bio: String,
        profile_image: String,
        resultHandler: (ResultHandler<ResultHandler.Success<LogoutResponse>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("name", name)
                put("bio", bio)
                put("profile_image", profile_image)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.update_Profile(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }



    fun updateUserName(
        user_id: Int,
        username: String,
        resultHandler: (ResultHandler<ResultHandler.Success<UserNameUpdate>>) -> Unit
    ) {
        viewModelScope.launch {

            resultHandler(ResultHandler.Loading)

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("username", username)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.updateUserName(requestBody).collect { result ->

                    when (result) {


                        is ResultHandler.Success -> {
                            resultHandler(ResultHandler.Success(result))
                        }

                        is ResultHandler.Error -> {
                            toast(message = result.message)
//                            GlobalSnackbar.show(result.message)
                            resultHandler(ResultHandler.Error(result.message))
                        }

                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                toast(message = e.message ?: "")
                resultHandler(ResultHandler.Error(e.message ?: ""))
            }
        }
    }

    private val _notificationPrefsState =
        MutableStateFlow(GetNotificationPrefsState())

    val notificationPrefsState: StateFlow<GetNotificationPrefsState> =
        _notificationPrefsState.asStateFlow()



    fun notification_Settings(
        user_id: Int,
        allow_notification: String,
        notification_type: String,
        status: String,
        resultHandler: (ResultHandler<ResultHandler.Success<NotificationSettings>>) -> Unit
    ) {





            viewModelScope.launch {

//                resultHandler(ResultHandler.Loading)

                _notificationPrefsState.update {
                    it.copy(isLoading = true)
                }

                val jsonObject = JSONObject().apply {
                    put("user_id", user_id)
                    put("allow_notification", allow_notification)
                    put("notification_type", notification_type)
                    put("status", status)
                }

                val requestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                try {
                    repo.notification_Settings(requestBody).collect { result ->
//                    resultCallback(result)

                        when (result) {
                            is ResultHandler.Success -> {
                                _notificationPrefsState.update {
                                    it.copy(
                                        isLoading = false,
                                        notificationPrefs = result.data.data ?: emptyList(),
                                        error = ""
                                    )
                                }
                            }

                            is ResultHandler.Error -> {
                                _notificationPrefsState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = result.message ?: "Unknown Error"
                                    )
                                }
                            }

                            is ResultHandler.Loading -> {
                                _notificationPrefsState.update {
                                    it.copy(isLoading = true)
                                }
                            }
                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    _notificationPrefsState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unexpected error"
                        )
                    }
                }
            }

    }


    private var blockPage = 1

    private var isBlockListLoading = false

    private var isBlockListLastPage = false

    // ============================
    // 🔥 GET REELS
    // ============================
    fun getBlockedList(
        user_id: Int,
        loadMore: Boolean = false
    ) {
        if (isBlockListLoading || isBlockListLastPage) return

        viewModelScope.launch {


            isBlockListLoading = true
            sharedRepository.setLoading(true)


            if (!loadMore) {
                blockPage = 1
                isBlockListLoading = false
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("page", blockPage)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.getBlockedList(requestBody).collect { result ->

                    when (result) {

                        is ResultHandler.Success -> {

                            val response = result.data
                            val newList = response.data

                            // ✅ Last page check
                            if (newList.isNullOrEmpty()) {
                                isBlockListLastPage = true
                            } else {
                                blockPage++
                            }

                            sharedRepository.setBlockedList(
                                newList = newList,
                                isFirstPage = !loadMore
                            )

                        }

                        is ResultHandler.Error -> {
                            sharedRepository.setError(result.message)
                        }

                        else -> Unit
                    }

                    isBlockListLoading = false
                    sharedRepository.setLoading(false)
                }

            } catch (e: Exception) {
                isBlockListLoading = false
                sharedRepository.setLoading(false)
                sharedRepository.setError(e.message ?: "Something went wrong")
            }
        }
    }



    private val _myInterest = MutableStateFlow(MyInterestState())

    val myInterest: StateFlow<MyInterestState> = _myInterest.asStateFlow()




    fun myInterest(
        user_id: Int,
        interest_id: String,
        status: String,
        resultHandler: (ResultHandler<ResultHandler.Success<MyInterestResponse>>) -> Unit
    ) {
        viewModelScope.launch {
//                resultHandler(ResultHandler.Loading)
            _myInterest.update {
                it.copy(isLoading = true)
            }

            val jsonObject = JSONObject().apply {
                put("user_id", user_id)
                put("interest_id", interest_id)
                put("status", status)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                repo.myInterest(requestBody).collect { result ->
//                    resultCallback(result)

                    when (result) {
                        is ResultHandler.Success -> {
                            _myInterest.update {
                                it.copy(
                                    isLoading = false,
                                    myInterestOrfs = result.data.data ?: emptyList(),
                                    error = ""
                                )
                            }
                        }

                        is ResultHandler.Error -> {
                            _myInterest.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Unknown Error"
                                )
                            }
                        }

                        is ResultHandler.Loading -> {
                            _myInterest.update {
                                it.copy(isLoading = true)
                            }
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                _myInterest.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unexpected error"
                    )
                }
            }
        }

    }




//    fun getAllInterests() {
//        viewModelScope.launch {
//            _allInterests.update { it.copy(isLoading = true) }
//            try {
//                repo.getAllInterests().collect { result ->
//                    when (result) {
//                        is ResultHandler.Success -> {
//                            _allInterests.update {
//                                it.copy(
//                                    isLoading = false,
//                                    interests = result.data.data ?: emptyList(),
//                                    error = ""
//                                )
//                            }
//                        }
//                        is ResultHandler.Error -> {
//                            _allInterests.update {
//                                it.copy(isLoading = false, error = result.message ?: "Unknown Error")
//                            }
//                        }
//                        is ResultHandler.Loading -> _allInterests.update { it.copy(isLoading = true) }
//                        else -> {}
//                    }
//                }
//            } catch (e: Exception) {
//                _allInterests.update { it.copy(isLoading = false, error = e.message ?: "Unexpected error") }
//            }
//        }
//    }

    private val _allInterests = MutableStateFlow(AllInterestsUiState())
    val allInterests: StateFlow<AllInterestsUiState> = _allInterests.asStateFlow()

}

data class GetNotificationPrefsState(
    var isLoading: Boolean = false,
    var notificationPrefs: List<NotificationSettingsData> = emptyList(),
    var error: String = ""
)


data class MyInterestState(
    val isLoading: Boolean = false,
    val myInterestOrfs: List<MyInterestResponseData> = emptyList(),
    val error: String = ""
)