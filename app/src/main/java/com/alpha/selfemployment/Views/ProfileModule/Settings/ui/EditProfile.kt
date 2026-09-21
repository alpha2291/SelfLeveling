package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.TextInputWithChange
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun Edit_Profile(
    settingsApiViewModel: SettingsApiViewModel = koinViewModel()
    ,appPrefs : AppPreferences = koinInject()
){




    var userName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    val navigator = LocalNavigator.current


    DisposableEffect(Unit) {

        userName = appPrefs.getUserName()
        name = appPrefs.getRealName()
        bio = appPrefs.getBio()

        onDispose {  }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(primaryWhite)
        , verticalArrangement = Arrangement.Top
        , horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
            , contentAlignment = Alignment.Center
        ){
            Image(painter = painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier.
                align(Alignment.CenterStart)
                    .shrinkClick {
                        navigator.pop()
                    }
                )

            zText("Edit Profile" , black1A , 24  , 0,modifier = Modifier.align(Alignment.Center))
        }


        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(8f)
                .padding(horizontal = 16.dp)
        )
        {

            spacer(4)

        Edit_Profile_Image(modifier = Modifier)

            spacer(4)

        zText("Username" , black1A , 14  , 1,modifier = Modifier)

            spacer(4)

        TextInputWithChange(
            value = userName,
            onValueChange = {
                userName = it
            },
            verified = if (appPrefs.getUserName() != userName) true else false,
//            onChangeClick = {
//
//            },
            onVerify = {
                settingsApiViewModel.updateUserName(
                    appPrefs.getUserId(),
                    username = userName
                ){
                    result ->

                    when (result){
                       is  ResultHandler.Success -> {
                           GlobalSnackbar.show(id = R.string.username_updated_successfully)
                           appPrefs.save_UserName(result.data.data.data.first().username)
                       }
                        is ResultHandler.Error -> {}
                        else -> {}

                    }
                }
            },
        )
            spacer(4)

        zText("You can change username only 30 days once" , gray48 , 12  , 2,modifier = Modifier)

            spacer(8)

        zText("Name" , black1A , 14  , 1,modifier = Modifier)

            spacer(4)

        OutlinedTextField(
            value = "",
            onValueChange = {
                if (it.length <= 30) {

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

            spacer(8)

        zText("Add Bio" , black1A , 14  , 1,modifier = Modifier)

            spacer(4)

        OutlinedTextField(
            value = "",
            onValueChange = {
                if (it.length <= 30) {

                }
            }
            ,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            )
            , placeholder = {
                zText("Enter bio", gray66, 12 , 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done , keyboardType = KeyboardType.Text)
            ,modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 108.dp)
        )


        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(8f)
                    .fillMaxHeight(.8f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(black1A)
                    .shrinkClick {

                        settingsApiViewModel.update_Profile(
                            user_id = appPrefs.getUserId(),
                            name = name,
                            bio = bio,
                            profile_image = "",
                        ){
                            result ->
                            when(result){
                                is ResultHandler.Success<*> -> {}
                                is ResultHandler.Error -> {}
                                else -> {}
                            }
                        }
                    }
                , contentAlignment = Alignment.Center
            ){
                zText("Save Changes" , primaryWhite , 14 , 0)
            }
        }

    }
}


@Composable
fun Edit_Profile_Image(
    modifier: Modifier
){



    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri ?: return@rememberLauncherForActivityResult

            // show image instantly (local preview)
           // profileVm.update_bitmapImage(uri)

            // upload to S3
           // viewModel.uploadProfileImage(uri)


        }


    Row(
        modifier = modifier
            .fillMaxWidth()
        , verticalAlignment = Alignment.CenterVertically
        , horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(primaryWhite)
        ){
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .clip(CircleShape)
                    .background(lightGreenEBF)
                , contentAlignment = Alignment.Center
            ){
                //if (profileUpload.progress in 1..99) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = primaryWhite
                    )
                //}else {
                    /// profile image

//                    LaunchedEffect(profileUpload.url ) {
//                        //println("HERE DOE THIS WORK")
//                        profileVm.update_ProfileImage(profileUpload.url ?: "")
//                    }

                    SubcomposeAsyncImage(
                        model = R.drawable.ic_launcher_background
                            //profileUpload.url ?: appPrefs.getProfileImage(),
                        ,modifier = Modifier
                            .fillMaxSize()
                        , contentDescription = ""
                        , contentScale = ContentScale.FillBounds
                    )
                    {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(gray48)
                                , contentAlignment = Alignment.Center
                            ){
                                Image(painter = painterResource(id = R.drawable.logoplaceholder),
                                    contentDescription = ""

                                    ,modifier = Modifier
                                        .fillMaxSize()
                                )
//                                                        Text(
//                                                            text = content[index]?.enquiry_details?.enquiry_by_username.takeIf { it?.isNotEmpty() == true }?.take(1)?.uppercase() ?: "",
//                                                            fontSize = constants.textUnit(14),
//                                                            fontFamily = constants.fontFamily(1),
//                                                            color = Color.Black
//                                                        )
//                            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                                contentDescription = "",modifier = Modifier
//                                    .matchParentSize())
                            }
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }
                //}
            }

            Image(painter = painterResource(R.drawable.profilepicture_editicon)
                , ""
                , modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .shrinkClick {
                        imagePickerLauncher.launch(arrayOf("image/*"))
                    }
            )

        }
    }
}


@Preview
@Composable
private fun TRESt() {
    Edit_Profile()
}
