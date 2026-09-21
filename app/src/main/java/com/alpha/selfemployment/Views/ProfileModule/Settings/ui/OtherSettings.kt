package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.CommonAlert
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.NumberEmailChangeVerifyBtm
import com.alpha.selfemployment.NumberInput
import com.alpha.selfemployment.NumberInputWithChange
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Startup.di.viewModels.AuthApiViewModel
import com.alpha.selfemployment.Startup.di.viewModels.AuthState
import com.alpha.selfemployment.Startup.di.viewModels.CredentialViewModel
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.CommonView.di.CommonViewModel
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.toast
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.utils
import com.alpha.selfemployment.zText
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactSettings(
    appPrefs: AppPreferences = koinInject()
    , authApiVm : AuthApiViewModel = org.koin.androidx.compose.koinViewModel(),
    viewModel : CredentialViewModel = org.koin.androidx.compose.koinViewModel()
    ){


    var mobileNumber by remember { mutableStateOf("") }
    var whatsAppNumber by remember { mutableStateOf("") }
    var emailId by remember { mutableStateOf("") }


    val btmsheetState = utils.numberemailchangeVerify.collectAsStateWithLifecycle()


    LaunchedEffect(appPrefs.getPhoneNumber()) {
        mobileNumber = appPrefs.getPhoneNumber()
//        whatsAppNumber = appPrefs.g
    }

    val registerApiState by authApiVm.registerState.collectAsStateWithLifecycle()


    LaunchedEffect(registerApiState) {
        if (registerApiState is UiState.Success){
           utils.open_numberemailchangeVerify()
        }

    }


    val verifyApiState by authApiVm.verifyState.collectAsStateWithLifecycle()


    LaunchedEffect(verifyApiState) {
        if (verifyApiState is UiState.Success){
            println("1234567890-0987654321")
            //authVm.change_AuthState(AuthState.Verify)




            GlobalSnackbar.show(id = R.string.number_changes_successfully)
            utils.close_numberemailchangeVerify()

            viewModel.clearOtp()
            viewModel.clearOtpError()
            GlobalSnackbar

        }

    }


    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = rememberNotchHeightDp().value)

                //.weight(.5f)
            , contentAlignment = Alignment.Center
        ){
            Icon(painterResource(R.drawable.left_arrow), "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick {
                       navigator.pop()
                    }
            )

            zText(str(R.string.contact), primaryBlack, 24, 0)
        }

        spacer(6)

        // mobile number

        zText(str(R.string.mobile_number) , primaryBlack , 14 , 1)

        spacer(4)


        NumberInputWithChange(
            value = mobileNumber,
            onValueChange = {
                mobileNumber = it
            },
            verified = mobileNumber != appPrefs.getPhoneNumber() && mobileNumber.length == 10
            , onChangeClick = {

            },
            onVerify = {
                authApiVm.register(
                    name = appPrefs.getUserName(),
                    phone_num_cc = "+91",
                    phone_num = mobileNumber,
                    device_id = "1234567890",
                    device_type = "Android",
                    device_token = ""
                )
            }
        )

        spacer(6)

        // whatsapp number

        zText(str(R.string.whatsapp_number) , primaryBlack , 14 , 1)

        spacer(4)

        NumberInputWithChange(
            value = whatsAppNumber,
            onValueChange = {
               whatsAppNumber = it
            },
            verified = false
            , onChangeClick = {

            },
            onVerify = {

            }
        )

        spacer(6)


        zText(str(R.string.email_id) , primaryBlack , 14 , 1)

        spacer(4)

        OutlinedTextField(
            value = emailId,
            onValueChange = {
                emailId = it
            }
            ,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            )
            , placeholder = {
                zText(str(R.string.enter_email_id), gray66, 12 , 2)
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                    , horizontalArrangement = Arrangement.Center
                ) {
                    //if (verified) {
                        Image(painter = painterResource(R.drawable.verified), "")
                   // }

                    spacer(4)

                    VerticalDivider(color = grayB8)

//                    Box(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .width(72.dp)
//                        , contentAlignment = Alignment.Center
//                    ){
//                        zText("Change" , black1A, 12 , 2)
//                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(72.dp)
                            .background(black1A)
                        , contentAlignment = Alignment.Center
                    ){
                        zText(str(R.string.verify) , primaryWhite, 12 , 2)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = grayB8,
                unfocusedBorderColor = grayB8,
            )
            ,modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)

        )

    }

    NumberEmailChangeVerifyBtm(number = mobileNumber)
}




data class NotificationSettingSubItems(
    var id : Int,
    var title : String,
    var isChecked : Boolean = true
)



@Composable
fun NotificationSettings(){


    var switch by remember { mutableStateOf(false) }

    val notificationSubSettings by utils.notificationSubSettings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
            .weight(.5f)
                .padding(horizontal = 16.dp)
            , contentAlignment = Alignment.Center
        )
        {
            Icon(
                painterResource(R.drawable.left_arrow), "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick {

                    }
            )

            zText("Notification", primaryBlack, 24, 0)
        }

        spacer(6)


        Row(
            modifier = Modifier.fillMaxWidth().weight(.5f) .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            zText("Allow Notification", primaryBlack, 16, 1)

            Switch(
                checked = switch,
                onCheckedChange = {
                    switch = !switch
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = green3A8,
                )
            )
        }



        Column(
            modifier = Modifier.fillMaxWidth().weight(7.5f) .padding(horizontal = 16.dp),
        ) {
            AnimatedVisibility(
                visible = switch,

            )
            {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    notificationSubSettings.forEachIndexed { index, items ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            zText(items.title, primaryBlack, 14, 3)

                            Checkbox(
                                checked = items.isChecked,
                                onCheckedChange = {
                                    utils.toggleNotificationSubSettings(items.id)
                                }, colors = CheckboxDefaults.colors(
                                    checkedColor = green3A8,
                                    checkmarkColor = primaryWhite,
                                    uncheckedColor = grayB8,
                                    disabledUncheckedColor = grayB8
                                )
                            )
                        }

                        spacer(4)
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)

            , verticalArrangement = Arrangement.SpaceEvenly
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()

            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
                    .height(56.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(black1A)

                , contentAlignment = Alignment.Center
            ){
                zText("Submit" , primaryWhite , 14 , 0)
            }
        }


    }
}


@Composable
fun DeleteAccount(
    settingsVm : SettingsApiViewModel = koinViewModel()
    ,commonViewModel: CommonViewModel = koinInject()
    ,appPrefs : AppPreferences = koinInject()
) {

    val optionsList = listOf(
        "Spam or Irrelevant Content",
        "Inappropriate Content",
        "Technical Issues or Bugs",
        "Privacy Violations",
        "Cultural Insensitivity",
        "Something Else"
    )

    var selectedOption by remember { mutableStateOf(-1) }
    var otherReason by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    val navigator = LocalNavigator.current

    val network by rememberNetworkStatus()

    val commonAlert by commonViewModel.commonAlert.collectAsStateWithLifecycle()


    Column {

        Box(
            modifier = Modifier
                .padding(vertical = rememberNotchHeightDp().value)
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
            , contentAlignment = Alignment.Center
        ) {

            Icon(
                painterResource(R.drawable.left_arrow),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick {
                        navigator.pop()
                    }
            )

            zText("Delete Account", primaryBlack, 24, 0)
        }

        LazyColumn(
        ) {
            item {


        Text(
            "Account Deletion is a Permanent Action",
           color =  redE54,
           fontSize =  textUnit(18),
            fontFamily = fontFamily(0),
//            lineHeight = textUnit(16),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

                spacer(6)

//        zText(
//            "This action is permanent and will remove all your data, including your saved searches, listings, and preferences.\n Deleting your account will remove all your information from our system. If you ever want to return, you’ll need to create a new account. \n This action is permanent and will remove all your data, including your saved searches, listings, and preferences.",
//            primaryBlack,
//            14,
//            2,
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//        )

                Text(
                    "This action is permanent and will remove all your data, including your saved searches, listings, and preferences.\n\n Deleting your account will remove all your information from our system. If you ever want to return, you’ll need to create a new account. \n\n This action is permanent and will remove all your data, including your saved searches, listings, and preferences.",
                    color =  primaryBlack,
                    fontSize =  textUnit(14),
                    fontFamily = fontFamily(2),
                    lineHeight = textUnit(24),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                spacer(6)

        zText(
            "Please select the main reason to deleting your account",
            primaryBlack,
            18,
            0,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

                spacer(6)

        optionsList.forEachIndexed { index, text ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shrinkClick { selectedOption = index },
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = selectedOption == index,
                    onClick = {
                        selectedOption = index
                        if (optionsList.lastIndex == selectedOption){
                            otherReason = ""
                        }
                        else {
                            otherReason = text
                        }
                    }
                )

                zText(text, primaryBlack, 14, 3)
            }
        }


        if (selectedOption == optionsList.lastIndex) {

            OutlinedTextField(
                value = otherReason,
                onValueChange = { otherReason = it },
                textStyle = TextStyle(
                    fontSize = textUnit(12),
                    fontFamily = fontFamily(3),
                    color = primaryBlack
                ),
                placeholder = {
                    zText("What else we need to know...", gray66, 12, 2)
                },
                modifier = Modifier.heightIn(min = 128.dp).fillMaxWidth().padding(horizontal = 16.dp)
            )
        }

                spacer(6)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = confirmDelete,
                onCheckedChange = { confirmDelete = it }
            )

            zText(
                "Yes, I want to permanently delete my account",
                black1A,
                14,
                3
            )
        }

                spacer(6)

                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .heightIn(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(redE54)
                        .shrinkClick {
                            when {
                                selectedOption == -1 -> { toast("Select Any Option") }
                                otherReason.isEmpty() -> { toast("Enter your Reason") }
                                !confirmDelete -> { toast("Tick to confirm you confirmation") }
                                else -> {
                                    commonViewModel.enable_Alert()
                                }
                            }
                        }

                    , contentAlignment = Alignment.Center
                ){
                    zText("Delete Account" , primaryWhite ,14 ,0)
                }

                spacer(6)

            }
        }


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
                    settingsVm.activate_Deactivate_Account(
                        user_id = appPrefs.getUserId(),
                        status = "1",
                        account_delete_type = selectedOption.toString(),
                        account_delete_sentence = otherReason,
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