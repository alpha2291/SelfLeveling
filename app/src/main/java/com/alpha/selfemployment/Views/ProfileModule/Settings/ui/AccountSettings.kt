package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.CommonAlert
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.di.CommonViewModel
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.zText
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AccountSettings(
    commonViewModel: CommonViewModel = koinInject(),
    settingsApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPrefs : AppPreferences = koinInject()
){

    val navigator = LocalNavigator.current

    val commonAlert by commonViewModel.commonAlert.collectAsStateWithLifecycle()

    val network by rememberNetworkStatus()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = rememberNotchHeightDp().value)
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f)
                .padding(horizontal = 16.dp)
            , contentAlignment = Alignment.Center
        ){
            Icon(painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick {
                        navigator.pop()
                    }
            )

            zText("Account Settings" , primaryBlack, 24, 0)
        }

        AccountSettingsItems(modifier = Modifier.fillMaxWidth().weight(9.5f) , onAccountSettingsClick = {
            index ->

            when(index){
                0 -> {
                    navigator.navigate(Screen.ContactSettings)
                }

                2 -> {

                    navigator.navigate(Screen.NotificationSettings)
                }
                4 -> {
                    navigator.navigate(Screen.DeleteAccount)
                }

                3 -> {
                    navigator.navigate(Screen.BlockListScreen)
                }
                5 -> {
                    commonViewModel.enable_Alert()
                }
            }
        })
    }

    if (commonAlert){
        CommonAlert(
            type = false,
            title = "Logout",
            description = "You’re about to log out. You can always come back and pick up where you left off. Come back soon!",
            cancelText = "Cancel",
            confirmText = "Logout",
            onCancel = {
                commonViewModel.disable_Alert()
            },
            onConfirm = {
                if (network == NetworkStatus.Online) {
                    settingsApiViewModel.logout(
                        appPrefs.getUserId()
                    ) { result ->
                        when (result) {
                            is ResultHandler.Success<*> -> {}
                            is ResultHandler.Error -> {}
                            else -> {}
                        }
                    }
                }
                else {
                    networkToast()
                }
            },
        )
    }


}


@Composable
fun AccountSettingsItems(modifier: Modifier , onAccountSettingsClick : (Int) -> Unit){

    val items = listOf(
        "Contact",
        "Share Profile",
        "Notification",
        "My Blocklist",
        "Delete Account",
        "Logout"
    )

    Column(
        modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        spacer(16)

        items.forEachIndexed { index, string ->
            Row(
                modifier = Modifier.shrinkClick{
                    onAccountSettingsClick(index)
                },
                verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.Start
            ) {
                zText(string , if (items.size - 1 == index) redE54 else  primaryBlack, 16 ,2)
            }

            spacer(4)
        }
    }
}