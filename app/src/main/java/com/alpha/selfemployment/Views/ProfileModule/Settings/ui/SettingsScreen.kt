package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.R
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.utils
import com.alpha.selfemployment.zText


data class MainSettingItems(
    var id : Int,
    var title : Int ,
    var icon : Int
)

@Composable
fun Settings(){

    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryWhite)
            .padding(top = rememberNotchHeightDp().value)
    ) {
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

            zText("Settings" , primaryBlack, 24, 0)
        }

        Settings_Components(modifier = Modifier.weight(9.5f),
            onSettingsClick = {
                id ->

                when (id) {
                    0 -> navigator.navigate(Screen.AccountSettings)
                    4 -> {
                        navigator.navigate(Screen.ChooseLanguageScreen)
                    }
                    1 ->{
                        navigator.navigate(Screen.MyInterestScreen)
                    }

                }

            })
    }
}


@Composable
fun Settings_Components(modifier: Modifier , onSettingsClick : (Int) -> Unit){

    val items by utils.settingsItems.collectAsStateWithLifecycle()

    Column(
        modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        spacer(16)

        items.forEachIndexed { index, items ->
            Row(
                modifier = Modifier
                    .shrinkClick{
                        onSettingsClick(
                            items.id
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.Start
            ) {
                Image(painter = painterResource(items.icon) , "", modifier = Modifier.size(24.dp))

                spacer(4)

                zText(str( items.title) , primaryBlack, 16 ,2)
            }

            spacer(4)
        }
    }
}



