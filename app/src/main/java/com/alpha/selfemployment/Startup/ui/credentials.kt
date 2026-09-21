package com.alpha.selfemployment.Startup.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.FullLoader
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.NumberInput
import com.alpha.selfemployment.OTP_TF
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Startup.di.viewModels.AuthApiViewModel
import com.alpha.selfemployment.Startup.di.viewModels.AuthState
import com.alpha.selfemployment.Startup.di.viewModels.CredentialViewModel
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberCountdownTimer
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.toast
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun CredentialScreen(
    authVm: CredentialViewModel = koinViewModel()
    ,authApiVm : AuthApiViewModel = koinViewModel()
    ,appPrefs : AppPreferences = koinInject()
){

    val authState by authVm.authStateHandler.collectAsStateWithLifecycle()

    val registerApiState by authApiVm.registerState.collectAsStateWithLifecycle()


    val verifyApiState by authApiVm.verifyState.collectAsStateWithLifecycle()


    val loginApiState by authApiVm.loginState.collectAsStateWithLifecycle()


    val navigator = LocalNavigator.current


    LaunchedEffect(registerApiState ,loginApiState , verifyApiState) {
        if (registerApiState is UiState.Success){
            authVm.change_AuthState(AuthState.Verify)
        }

        if (loginApiState is UiState.Success){
            authVm.change_AuthState(AuthState.Verify)
        }

        if (verifyApiState is UiState.Success){
            println("1234567890-0987654321")
            navigator.clearAndNavigate(Screen.LaunchScreen)
            //authVm.change_AuthState(AuthState.Verify)
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(green3A8)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(.3f)
            , contentAlignment = Alignment.Center
        ){
            Image(painter = painterResource(R.drawable.logoplaceholder) , "")
        }


        if (registerApiState is UiState.Loading) {
            FullLoader()
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(.7f)
                .clip(RoundedCornerShape(topStart = 36.dp , topEnd = 36.dp))
                .background(primaryWhite)
        ) {
            when (authState){
                AuthState.Register -> Register(
                    authVm,
                    onRegisterClick =
                        {
                            if (registerApiState is UiState.Loading){

                            }
                            else {
                                authVm.set_AuthStateFlow(true)
                                authApiVm.register(
                                    name = authVm.name.value,
                                    phone_num_cc = "+91",
                                    phone_num = authVm.mobileNumber.value,
                                    device_id = "1234567890",
                                    device_type = "Android",
                                    device_token = ""
                                )
                            }
                        }
                )
                AuthState.Login -> Login(
                    authVm,
                    onLoginClick = {
                        if (loginApiState is UiState.Loading){

                        }
                        else {
                            authVm.set_AuthStateFlow(false)
                            authApiVm.login(
                                phone_num_cc = "+91",
                                phone_num = authVm.mobileNumber.value,
                                device_id = "1234567890",
                                device_type = "Android",
                                device_token = ""
                            )

                        }
                    }
                )
                else -> Verify(
                    authVm,
                    onVerifyClick = {
                        otp ->
                        if (verifyApiState is UiState.Loading){

                        }
                        else {
                            authApiVm.verify(
                                user_id = if(appPrefs.getUserId() == -1) 0 else appPrefs.getUserId(),
                                phone_num = authVm.mobileNumber.value,
                                new_phone_num = "",
                                whatsapp_num = "",
                                email = "",
                                otp = otp,
                                device_id = "",
                                device_type = "Android",
                                device_token = "",
                            )
                        }
                    }
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.credentialsbackground)
            , ""
            , modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .fillMaxHeight(.3f)
        )
    }
}


@Composable
fun Login(
    authUiVm : CredentialViewModel
    ,onLoginClick : () -> Unit
){

    val network by rememberNetworkStatus()

    val number by authUiVm.mobileNumber.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        spacer(4)

        zText("Login" , primaryBlack, 24, 0,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )



        zText("Welcome back! We're excited to have you here again."
            , primaryBlack
            , 14
            , 2
            , textAlign = TextAlign.Center
            , modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        spacer(4)

        zText("Mobile Number"
            , primaryBlack
            , 14
            , 1
            , modifier = Modifier.align(Alignment.Start)
        )

        NumberInput(
            value = number,
            onValueChange = {
                authUiVm.addMobileNumber(it)
            }
        )

        spacer(8)


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(black1A)
                .shrinkClick {
                    if (network == NetworkStatus.Online) {
                        onLoginClick()
                    }
                    else {
                        networkToast()
                    }
                }
            , contentAlignment = Alignment.Center
        ){
            zText("Login" , primaryWhite, 14, 0,
                modifier = Modifier
            )
        }

        spacer(8)

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = primaryBlack, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Don't have an account? ")
                }

                withStyle(style = SpanStyle(color = green3A8, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Register")
                }
            }
            , modifier = Modifier  .shrinkClick {
                authUiVm.clearMobileNumber()
                authUiVm.clearName()
                authUiVm.change_AuthState(AuthState.Register)
            }
                .align(Alignment.CenterHorizontally)
        )

    }
}


@Composable
fun Register(
    authUiVm : CredentialViewModel
    ,onRegisterClick : () -> Unit
){

    val name by authUiVm.name.collectAsStateWithLifecycle()
    val number by authUiVm.mobileNumber.collectAsStateWithLifecycle()

    val network by rememberNetworkStatus()


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        spacer(4)

        zText("Register" , primaryBlack, 24, 0,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        zText("Start your journey by registering now. Fill in your details to create an account."
            , primaryBlack
            , 14
            , 2
            , textAlign = TextAlign.Center
            , modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        spacer(4)

        zText("Name"
            , primaryBlack
            , 14
            , 1
            , modifier = Modifier.align(Alignment.Start)
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                if (it.length <= 30) {
                    authUiVm.addName(it)
                }
            }
            ,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            )
            , placeholder = {
                zText("Enter name", gray66, 12 , 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done , keyboardType = KeyboardType.Text)
            ,modifier = Modifier.fillMaxWidth()
        )

        spacer(4)

        zText("Mobile Number"
            , primaryBlack
            , 14
            , 1
            , modifier = Modifier.align(Alignment.Start)
        )

        NumberInput(
            value = number,
            onValueChange = {
                authUiVm.addMobileNumber(it)
            }
        )

        spacer(4)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(black1A)
                .shrinkClick{
                    if (network == NetworkStatus.Online) {
                        onRegisterClick()
                    }
                    else {
                        networkToast()
                    }
                }
            , contentAlignment = Alignment.Center
        ){
            zText("Register" , primaryWhite, 14, 0,
                modifier = Modifier
            )
        }

        spacer(8)

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = primaryBlack, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Already have an Account? ")
                }

                withStyle(style = SpanStyle(color = green3A8, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Login")
                }
            }
            , modifier = Modifier
                .shrinkClick{

                    authUiVm.clearMobileNumber()
                    authUiVm.clearName()
                    authUiVm.change_AuthState(AuthState.Login)
                }
                .align(Alignment.CenterHorizontally)
        )


        spacer(4)

    }
}



@Composable
fun Verify(
    viewModel : CredentialViewModel
    ,authApiVm : AuthApiViewModel = koinViewModel()
    ,onVerifyClick : (String) -> Unit
){

    val otp by viewModel.otp.collectAsStateWithLifecycle()

    val otpError by viewModel.otpError.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val mobile by viewModel.mobileNumber.collectAsStateWithLifecycle()

    val timer = rememberCountdownTimer(10)


    val authStateFlow by viewModel.authStateFlow.collectAsStateWithLifecycle()

    val registerApiState by authApiVm.registerState.collectAsStateWithLifecycle()

    val network by rememberNetworkStatus()


    DisposableEffect(registerApiState) {

        if (registerApiState is UiState.Success){
            timer.restart()
        }
        onDispose {
            timer.reset()
        }
    }



    DisposableEffect(Unit) {
        timer.start()
        onDispose {
            timer.reset()
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        spacer(8)

        zText("Verification" , primaryBlack, 24, 0,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        zText("Start your journey by registering now. Fill in your details to create an account."
            , primaryBlack
            , 14
            , 2
            , textAlign = TextAlign.Center
            , modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        spacer(4)


        OTP_TF(
            otp = otp,
            onOtpChange = {
                viewModel.addOtp(it)
            },
            isError = otpError,
            modifier = Modifier
        )

        if (otpError) {
            zText(
                "Please enter valid OTP",
                redE54,
                14,
                2,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Row (
            modifier = Modifier
                .size(84.dp, 32.dp )
                .background(lightGreenEBF)
            , verticalAlignment = Alignment.CenterVertically
            , horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Icon(painter = painterResource(R.drawable.clock_timer) , "")

            zText(timer.time.value , green3A8 , 14 ,1)
        }

        spacer(8)

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = primaryBlack, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Didn’t receive ?")
                }

                withStyle(style = SpanStyle(color = if (timer.isFinished.value)green3A8 else green3A8.copy(.5f), fontSize = textUnit(14), fontFamily = fontFamily(2))){
                    append("Resend")
                }
            }
            , modifier = Modifier
                .shrinkClick {
                    if (network == NetworkStatus.Online) {
                        if (authStateFlow) {
                            // register
                            authApiVm.register(
                                name = name,
                                phone_num_cc = "+91",
                                phone_num = mobile,
                                device_id = "1234567890",
                                device_type = "Android",
                                device_token = ""
                            )
                        } else {
                            // login
                        }
                    }
                    else {
                        networkToast()
                    }


                }
                .align(Alignment.CenterHorizontally)
        )

        spacer(8)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(black1A)
                .shrinkClick{
                    if (network == NetworkStatus.Online) {
                        onVerifyClick(
                            otp
                        )
                    }
                    else {
                        networkToast()
                    }
                }
            , contentAlignment = Alignment.Center
        ){
            zText("Verify" , primaryWhite, 14, 0,
                modifier = Modifier
            )
        }



    }
}