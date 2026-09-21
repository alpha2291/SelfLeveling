package com.alpha.selfemployment.Views.Notitifcation.di.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class NotificationTabRow(
    var id : Int,
    var title : String
)

class NotificationUIViewModels : ViewModel(){


    private val _notificationTabRowItems = MutableStateFlow(listOf(
        NotificationTabRow(
            1,
            "All"
        ),
        NotificationTabRow(
            2,
            "Enquiries"
        ),
        NotificationTabRow(
            3,
            "Likes"
        ),
        NotificationTabRow(
            4,
            "Comments"
        ),
        NotificationTabRow(
            5,
            "Follows"
        ),

    ))


    val notificationTabRowItems : StateFlow<List<NotificationTabRow>> = _notificationTabRowItems.asStateFlow()

    private var _currentSeletedNotificationTabRowItem = MutableStateFlow(1)
    var currentSeletedNotificationTabRowItem : StateFlow<Int> = _currentSeletedNotificationTabRowItem.asStateFlow()

    fun selectNotificationItem(value : Int){
        _currentSeletedNotificationTabRowItem.value = value
    }

    fun clearNotificationItem(){
        _currentSeletedNotificationTabRowItem.value = 1
    }


}