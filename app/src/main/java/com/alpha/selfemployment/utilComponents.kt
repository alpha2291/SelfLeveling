package com.alpha.selfemployment

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.fastCbrt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.Startup.di.viewModels.AuthApiViewModel
import com.alpha.selfemployment.Startup.di.viewModels.CredentialViewModel
import com.alpha.selfemployment.Views.CommonView.di.CommonViewModel
import com.alpha.selfemployment.Views.Explore.di.ExploreAPIViewModel
import com.alpha.selfemployment.Views.Explore.di.ExploreUIViewModel
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.gray96
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.grayE8
import com.alpha.selfemployment.ui.theme.grayF4
import com.alpha.selfemployment.ui.theme.grayFB
import com.alpha.selfemployment.ui.theme.brandBlue
import com.alpha.selfemployment.ui.theme.lightBlueF0
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.ui.theme.redFC
import com.alpha.selfemployment.utils.Companion.onItemSelected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.collections.chunked
import kotlin.collections.forEach
import kotlin.math.absoluteValue


@Composable
fun NumberInput(
    value : String,
    onValueChange : (String) -> Unit,
    verified : Boolean = false
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp , grayB8, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.5f)
                .background(grayF4)
            , contentAlignment = Alignment.Center
        ){
            zText("+91", gray66, 12 , 2)
        }

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= 10) {
                    onValueChange(it)
                }
            }
            ,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            )
            , placeholder = {
                zText("Enter mobile number", gray66, 12 , 2)
            },
            trailingIcon = {
                if (verified){
                    Image(painter = painterResource(R.drawable.verified) , "")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
            ,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done , keyboardType = KeyboardType.NumberPassword)
            ,modifier = Modifier
                .fillMaxHeight()
                .weight(8.5f)
        )
    }
}

/*@Composable
fun NumberInputWithChange(
    value: String,
    onValueChange: (String) -> Unit,
    verified: Boolean = false,
    onChangeClick: () -> Unit,
    onVerify: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, grayB8, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.5f)
                .background(grayF4),
            contentAlignment = Alignment.Center
        ) {
            zText("+91", gray66, 12, 2)
        }

        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText("Enter mobile number", gray66, 12, 2)
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (verified) {
                        Image(painter = painterResource(R.drawable.verified), "")
                    }
                    spacer(4)
                    VerticalDivider(color = grayB8)
                    if (!verified) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .then(
                                    if (value.isNotEmpty())
                                        Modifier.shrinkClick {
                                            onChangeClick()
                                            // Focus the field and show keyboard
                                            focusRequester.requestFocus()
                                            keyboard?.show()
                                        }
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            zText(
                                "Change",
                                if (value.isEmpty()) black1A.copy(.5f) else black1A,
                                12, 2
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .background(black1A)
                                .shrinkClick { onVerify() },
                            contentAlignment = Alignment.Center
                        ) {
                            zText("Verify", primaryWhite, 12, 2)
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxHeight()
                .weight(8.5f)
                .focusRequester(focusRequester)  // attach focusRequester to field
        )
    }
}*/



@Composable
fun NumberInputWithChange(
    value: String,
    onValueChange: (String) -> Unit,
    verified: Boolean = false,
    onChangeClick: () -> Unit,
    onVerify: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // Control cursor position internally
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, grayB8, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.5f)
                .background(grayF4),
            contentAlignment = Alignment.Center
        ) {
            zText("+91", gray66, 12, 2)
        }

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                if (it.text.length <= 10) {
                    textFieldValue = it
                    onValueChange(it.text)
                }
            },
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText("Enter mobile number", gray66, 12, 2)
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (verified) {
                        Image(painter = painterResource(R.drawable.verified), "")
                    }
                    spacer(4)
                    VerticalDivider(color = grayB8)
                    if (!verified) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .then(
                                    if (value.isNotEmpty())
                                        Modifier.shrinkClick {
                                            onChangeClick()
                                            // Move cursor to end before focusing
                                            textFieldValue = TextFieldValue(
                                                text = value,
                                                selection = TextRange(value.length)
                                            )
                                            focusRequester.requestFocus()
                                            keyboard?.show()
                                        }
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            zText(
                                "Change",
                                if (value.isEmpty()) black1A.copy(.5f) else black1A,
                                12, 2
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .background(black1A)
                                .shrinkClick { onVerify() },
                            contentAlignment = Alignment.Center
                        ) {
                            zText("Verify", primaryWhite, 12, 2)
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done , keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxHeight()
                .weight(8.5f)
                .focusRequester(focusRequester)
        )
    }
}



@Composable
fun TextInputWithChange(
    value: String,
    onValueChange: (String) -> Unit,
    verified: Boolean = false,
    //onChangeClick: () -> Unit,
    onVerify: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // Control cursor position internally
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, grayB8, RoundedCornerShape(4.dp))
    ) {

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onValueChange(it.text)
            },
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText("Enter username", gray66, 12, 2)
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (verified) {
                        Image(painter = painterResource(R.drawable.verified), "")
                    }
                    spacer(4)
                    VerticalDivider(color = grayB8)
                    if (!verified) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .then(
                                    if (value.isNotEmpty())
                                        Modifier.shrinkClick {
                                           // onChangeClick()
                                            // Move cursor to end before focusing
                                            textFieldValue = TextFieldValue(
                                                text = value,
                                                selection = TextRange(value.length)
                                            )
                                            focusRequester.requestFocus()
                                            keyboard?.show()
                                        }
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            zText(
                                "Change",
                                if (value.isEmpty()) black1A.copy(.5f) else black1A,
                                12, 2
                            )
                        }
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .background(black1A)
                                .shrinkClick { onVerify() },
                            contentAlignment = Alignment.Center
                        ) {
                            zText("Verify", primaryWhite, 12, 2)
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
    }
}



@Composable
fun OTP_TF(
    otp: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean, // pass Utils.signUp_LoginVM.otpFail.value here
    modifier: Modifier = Modifier
)
{
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = otp,
        onValueChange = {
            if (it.length <= 4) {
                onOtpChange(it)
                //Utils.signUp_LoginVM.otpFail.value = false
                // Utils.signUp_LoginVM.otpValue.value = it
            }
            else it.take(4) },
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done  // Prevents jumping to the next field
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusRequester.freeFocus()
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        decorationBox = {
            Row(modifier = Modifier
                .fillMaxWidth( 1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically)
            {
                repeat(4) {
                        index->
                    val char = when{
                        index >= otp.length->""
                        else -> otp[index].toString()
                    }
                    var isFocused = otp.length == index

                    Box(
                        contentAlignment = Alignment.Center, // Center content within the box
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                if (isError) {
                                    Color.Red
                                } else {
                                    if (isFocused) brandBlue else if (char.isNotEmpty()) grayB8 else Color.LightGray
                                }
                                , shape = RoundedCornerShape(8.dp)
                            )

                    )
                    {
                        Text(
                            text = char,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
//                            fontFamily = FontFamily(Font(R.font.robotoreg))
                        )
                    }
                }
            }
        },
        textStyle = TextStyle(color = Color.Black,
            fontWeight = FontWeight.Bold,))
//            fontFamily = FontFamily(Font(R.font.robotoreg)),
//            fontSize = if(Utils.isTabDevice(Utils.activity)) 20.ScaledSp else 16.ScaledSp)
//    )
}



data class CountdownTimerState(
    val time: State<String>,
    val isFinished: State<Boolean>,
    val start: () -> Unit,
    val restart: () -> Unit,
    val reset: () -> Unit
)



@Composable
fun rememberCountdownTimer(startSeconds: Int = 180): CountdownTimerState {

    var timeLeft by remember { mutableStateOf(startSeconds) }
    var triggerRestart by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }

    val start: () -> Unit = {
        if (!started) {
            started = true
        }
    }

    val restart: () -> Unit = {
        timeLeft = startSeconds
        finished = false
        started = true
        triggerRestart++
    }

    // ✅ Reset function
    val reset: () -> Unit = {
        timeLeft = startSeconds
        finished = false
        started = false
    }

    LaunchedEffect(triggerRestart, started) {
        if (!started) return@LaunchedEffect

        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        finished = true
        started = false
    }

    val formattedTime = remember(timeLeft) {
        String.format("%02d:%02d", timeLeft / 60, timeLeft % 60)
    }

    return CountdownTimerState(
        time = rememberUpdatedState(formattedTime),
        isFinished = rememberUpdatedState(finished),
        start = start,
        restart = restart,
        reset = reset // add here
    )
}




@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderText : String,
    onClear : () -> Unit,
    modifier: Modifier
){
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        }
        ,
        textStyle = TextStyle(
            fontSize = textUnit(12),
            fontFamily = fontFamily(3),
            color = primaryBlack
        )
        , placeholder = {
            zText(placeHolderText, gray96 , 14 , 2)
        },
        leadingIcon = {
            Icon(painterResource(R.drawable.search) , "" , tint = gray96,
                modifier = Modifier
            )
        }
        , trailingIcon = {
            if (value.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.clearsearch), "",
                    modifier = Modifier
                        .shrinkClick {
                            onClear()
                        }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = primaryWhite,
            unfocusedContainerColor = primaryWhite,
            focusedTextColor = primaryBlack,
            unfocusedTextColor = grayB8,
            focusedBorderColor = primaryBlack,
            unfocusedBorderColor = grayB8
        ),
        modifier = modifier
    )
}



@Composable
fun ExpandableBio(
    bio: String,
    collapsedCharCount: Int = 80
) {
    var isExpanded by remember { mutableStateOf(false) }

    val isLong = bio.length > collapsedCharCount

    val displayedText = when {
        isExpanded -> bio
        isLong -> bio.take(collapsedCharCount)
        else -> bio
    }

    val annotatedString = buildAnnotatedString {


        withStyle(
            style = SpanStyle(
                color = primaryWhite,
                textUnit(12),
                fontFamily = fontFamily(3)
            )
        ) {
            append(displayedText)
        }

        if (isLong) {
            append(" ")

            pushStringAnnotation(
                tag = "TOGGLE",
                annotation = "toggle"
            )

            withStyle(
                style = SpanStyle(
                    color = primaryWhite,
                    textUnit(12),
                    fontFamily = fontFamily(0)
                )
            ) {
                append(
                    if (isExpanded) "See less"
                    else "... See more"
                )
            }

            pop()
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "TOGGLE",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                isExpanded = !isExpanded
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}



@Composable
fun ExpandableMessage(
    bio: String,
    collapsedCharCount: Int = 150,

) {
    var isExpanded by remember { mutableStateOf(false) }

    val isLong = bio.length > collapsedCharCount

    val displayedText = when {
        isExpanded -> bio
        isLong -> bio.take(collapsedCharCount)
        else -> bio
    }

    val annotatedString = buildAnnotatedString {


        withStyle(
            style = SpanStyle(
                color = primaryWhite,
                textUnit(12),
                fontFamily = fontFamily(3)
            )
        ) {
            append(displayedText)
        }

        if (isLong) {
            append(" ")

            pushStringAnnotation(
                tag = "TOGGLE",
                annotation = "toggle"
            )

            withStyle(
                style = SpanStyle(
                    color = primaryWhite,
                    textUnit(12),
                    fontFamily = fontFamily(0)
                )
            ) {
                append(
                    if (isExpanded) "read less"
                    else "... read more"
                )
            }

            pop()
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            textAlign = TextAlign.Start
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "TOGGLE",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                isExpanded = !isExpanded
            }
        },
        modifier = Modifier
            //.padding(horizontal = 24.dp)
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateBtmSheet(
    enable : Int
){}



data class PostFormats(
    var id : Int,
    var title : Int,
    val icon : Int
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostUploadFormatBtmSheet(
    onVideoClick : () -> Unit,
    onArticleClick : () -> Unit,
    onYoutubeVideoClick : () -> Unit
){

    val btmState by utils.postUploadFormat.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var optionList = listOf(
        PostFormats(
            id = 0,
            title = R.string.videos,
            icon = R.drawable.post_video
        ),
        PostFormats(
            id = 1,
            title = R.string.articles,
            icon = R.drawable.post_articles
        ),
        PostFormats(
            id = 2,
            title = R.string.youtube_video,
            icon = R.drawable.post_youtube
        ),

    )

    if (btmState) {
        ModalBottomSheet(
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            onDismissRequest = {
                utils.close_PostUploadBtm()
            },
//            dragHandle = {},
            containerColor = primaryWhite
        ) {

            LazyVerticalGrid(
                GridCells.Fixed(2)
                , modifier = Modifier.fillMaxWidth()
//                    .height()
                , contentPadding = PaddingValues(16.dp)
                , horizontalArrangement = Arrangement.spacedBy(16.dp)
                , verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item(span = {GridItemSpan(maxLineSpan)} ) {
                    Row(
                        modifier = Modifier.
                        fillMaxWidth()
                        , verticalAlignment = Alignment.CenterVertically
                        , horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        zText(str(R.string.upload) , black1A , 24 , 0)

                        Image(painter = painterResource(R.drawable.close) , "" ,
                            modifier = Modifier.size(32.dp).shrinkClick {
                                utils.close_PostUploadBtm()
                            })
                    }
                }

                itemsIndexed(optionList){
                    index , item ->


                    Box(
                        modifier = Modifier
                            .size(156.dp)
                            .border(1.dp , gray96 , RoundedCornerShape(6.dp))
                            .shrinkClick {
                                when (item.id) {
                                    0 -> onVideoClick()
                                    1 -> onArticleClick()
                                    2 -> onYoutubeVideoClick()
                                }
                            }
                        , contentAlignment = Alignment.Center
                    ){
                        Column(
                            modifier = Modifier.fillMaxSize()
                            , verticalArrangement = Arrangement.SpaceEvenly
                            , horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(painter = painterResource(item.icon) , "",
                                modifier = Modifier.size(64.dp))

                            zText(str(item.title) , black1A , 16 , 0)
                        }
                    }
                }
            }
        }
    }
}



data class ReelsBtmOptions(
    var id : Int,
    var title : Int,
    val icon : Int
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsOptionBtmSheet(
    onNotInterestClick : () -> Unit,
    onReportClick : () -> Unit
){

    val btmState by utils.reelsOptionsBtmSheet.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var optionList = listOf(
        ReelsBtmOptions(
            id = 0,
            title = R.string.share,
            icon = R.drawable.share
        ),
        ReelsBtmOptions(
            id = 1,
            title = R.string.not_interested,
            icon = R.drawable.notinterested
        ),
        ReelsBtmOptions(
            id = 2,
            title = R.string.report,
            icon = R.drawable.report
        ),

        )

    if (btmState) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                utils.close_ReelsOptionsBtm()
            },
            containerColor = primaryWhite
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                optionList.forEachIndexed { index, options ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shrinkClick {
                                when (options.id){
                                    0 -> {}
                                    1 -> {
                                        onNotInterestClick()
                                        println("not interest click")
                                    }
                                    2 -> {
                                        onReportClick()
                                        println("report click")
                                    }
                                }

                            }
                        , verticalAlignment = Alignment.CenterVertically
                        , horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(options.icon)
                            , ""
                            , modifier = Modifier.size(32.dp)
                        )

                        spacer(6)

                        zText(str(options.title) , black1A , 14,2)
                    }

                    spacer(12)
                }

            }
        }
    }
}





data class OwnPostOptions(
    var id : Int,
    var title : Int,
    val icon : Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnPostOptionsBtm(
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
){

    val btmState by utils.ownPostOptionsBtmSheet.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var optionList = listOf(
        OwnPostOptions(
            id = 0,
            title = R.string.share,
            icon = R.drawable.share
        ),
        OwnPostOptions(
            id = 1,
            title = R.string.edit,
            icon = R.drawable.edit_post
        ),
        OwnPostOptions(
            id = 2,
            title = R.string.delete_post,
            icon = R.drawable.delete_post
        ),

        )

    if (btmState) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                utils.close_OwnPostOptionsBtm()
            },
            containerColor = primaryWhite
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                optionList.forEachIndexed { index, options ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shrinkClick {
                                when (options.id){
                                    0 -> {
                                        /// share
                                    }
                                    1 -> {
                                        onEditClick()
                                        println("edit click")
                                    }
                                    2 -> {
                                        onDeleteClick()
                                        println("delete click")
                                    }
                                }

                            }
                        , verticalAlignment = Alignment.CenterVertically
                        , horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(options.icon)
                            , ""
                            , modifier = Modifier.size(32.dp)
                        )

                        spacer(6)

                        zText(str(options.title) , black1A , 14,2)
                    }

                    spacer(12)
                }

            }
        }
    }
}





data class ReportBtmSheetItems(
    var id : Int,
    var title : Int,
    var isSelected : Boolean = true
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBtmSheet(){

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val showBtmSheet by utils.invokeReportBtmSheet.collectAsStateWithLifecycle()

    val sheetOptions by utils.reportOptionsItems.collectAsStateWithLifecycle()

    val getSelectedOption = sheetOptions.firstOrNull { it.isSelected }?.id ?: 0

    var somethingElseContent = remember { mutableStateOf("") }

    if (showBtmSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
              utils.revokeReportBtm()
            }
            , containerColor = primaryWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // title

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp)
                    , contentAlignment = Alignment.CenterStart
                ){
                    zText(str(R.string.why_are_you_reporting) , black1A , 24 , 0)
                }

                spacer(4)

                // content

                sheetOptions.forEachIndexed { index, items ->
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp)
                            .fillMaxWidth()
                        , verticalAlignment = Alignment.CenterVertically
                        , horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        zText(str(items.title), black1A , 14 , 3)

                        RadioButton(
                            selected = items.isSelected ,
                            onClick = {
                                utils.toggleReportOptionItems(items.id)
                            }
                        )
                    }

                    spacer(4)
                }

                spacer(4)
                println("getselectedoption -- $getSelectedOption")

                AnimatedVisibility(
                    visible = getSelectedOption == 7
                )
                {
                    OutlinedTextField(
                        value = somethingElseContent.value,
                        onValueChange = {
                            somethingElseContent.value = it
                        },
                        textStyle = TextStyle(
                            fontSize = textUnit(12),
                            fontFamily = fontFamily(3),
                            color = primaryBlack
                        ),
                        placeholder = {
                            zText("Enter your own reason" , grayB8 , 12 , 3)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = primaryWhite,
                            unfocusedContainerColor = primaryWhite,
                            focusedBorderColor = black1A,
                            unfocusedBorderColor = grayB8,
                            focusedTextColor = primaryBlack,
                            unfocusedTextColor = primaryBlack
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().heightIn(min = 120.dp)

                    )

                    spacer(4)
                }


                spacer(4)

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = primaryBlack, fontSize = textUnit(16), fontFamily = fontFamily(1))){
                            append("Upload Proof")
                        }

                        withStyle(style = SpanStyle(color = gray66, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                            append("(Optional)")
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp , black1A , RoundedCornerShape(4.dp))
                    , verticalAlignment = Alignment.CenterVertically
                    , horizontalArrangement = Arrangement.Center
                ) {
                    spacer(2)

                    Image(painter = painterResource(R.drawable.upload_proof) , "",
                        modifier = Modifier.size(16.dp))

                    spacer(2)

                    zText("Click to Upload" , black1A, 14 ,1)

                    spacer(2)
                }

                spacer(4)

                HorizontalDivider()

                spacer(4)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brandBlue)
                    , contentAlignment = Alignment.Center
                ){
                    zText("Submit Report" , primaryWhite , 14 , 0)
                }

                spacer(4)

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubeReelsPlayerBtm(
){

    val btmState by utils.youtubeReelsFullView.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)


    if (btmState.isNotEmpty()) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                //utils.close_youtubeReelBtm()
            },
            containerColor = primaryBlack
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
                , contentAlignment = Alignment.Center
            ) {

                Image(painter = painterResource(R.drawable.clearsearch) , "",
                    modifier = Modifier
                        .padding(top = rememberNotchHeightDp().value , end = rememberNotchHeightDp().value)
                        .align(Alignment.TopEnd)
                        .shrinkClick {
                            utils.close_youtubeReelBtm()
                        }
                )
                YoutubePlayerScreen(
                    videoId = btmState,
                    isActive = true,
                    modifier = Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FullLoader(){
    BasicAlertDialog(
        onDismissRequest = {

        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(.6f))
            , contentAlignment = Alignment.Center
        ){
            CircularWavyProgressIndicator()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAlert(
    type : Boolean
    , title: String
    , description : String
    ,cancelText : String
    ,confirmText : String
    , onCancel : () -> Unit
    , onConfirm : () -> Unit
    ,commonVm : CommonViewModel = koinInject()
){

        BasicAlertDialog(
            onDismissRequest = {}
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(primaryWhite)
                    .padding(16.dp)
                , contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    zText(
                        title, primaryBlack, 16, 0, textAlign = TextAlign.Center
                    )


                    zText(
                        description, gray48, 12, 3, textAlign = TextAlign.Center
                    )

                    spacer(2)

                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    )
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(124.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    grayE8
                                )
                                .shrinkClick {
                                    commonVm.disable_Alert()
                                    onCancel()
                                }
                            , contentAlignment = Alignment.Center
                        ) {
                            zText(cancelText, gray48, 14, 0)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(124.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (type) black1A else redE54
                                )
                                .shrinkClick {
                                    commonVm.disable_Alert()
                                    onConfirm()
                                }, contentAlignment = Alignment.Center
                        ) {
                            zText(confirmText, primaryWhite, 14, 0)
                        }
                    }

                    spacer(4)

                }
            }
        }

}



@Composable
fun OverlayBottomBar(
    items: List<BtmBarItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            //.padding(horizontal = 12.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth().fillMaxHeight()

                //.shadow(10.dp, RoundedCornerShape(16.dp))
                .background(primaryWhite, RoundedCornerShape(16.dp))
//                .padding(vertical = 10.dp)
            , verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            items.forEach { item ->

                val isSelected = item.id == selectedItem

                Column(
                    modifier = Modifier
                        .clickable {
                            onItemSelected(item.id)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(
                            if (isSelected) item.selectedIcon else item.unSelectedIcon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    zText(
                        str(item.title),
                        if (isSelected) primaryBlack else primaryBlack.copy(.5f),
                        12,
                        0
                    )
                }
            }
        }
    }
}



@Composable
fun EnhancedGlassmorphismCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Background blur simulation
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        // Glass surface
        Card(
            modifier = Modifier
                .matchParentSize()
                ,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Row (
                modifier = Modifier.padding(28.dp),
                content = content
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreSearchField(
    exploreUIVm: ExploreUIViewModel = koinViewModel(),
    exploreAPIVm: ExploreAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject(),
    appPrefs: AppPreferences = koinInject(),
    modifier: Modifier = Modifier
) {
    val query by exploreUIVm.exploreSearchText.collectAsStateWithLifecycle()
    val searchSuggestions by sharedRepo.postCommonItems.collectAsStateWithLifecycle()
    val profileSearchData by sharedRepo.profileSearch.collectAsStateWithLifecycle()
    val network by rememberNetworkStatus()

    val typeOptions = listOf(R.string.business, R.string.profile)
    var selectedType by remember { mutableStateOf(R.string.business) }

    var mainExpanded by remember { mutableStateOf(false) }
    var miniExpanded by remember { mutableStateOf(false) }

    val navigator = LocalNavigator.current

    // Debounced API call
    LaunchedEffect(query.length, selectedType, network) {
        if (network == NetworkStatus.Online) {
            kotlinx.coroutines.delay(400)

            if (query.isNotBlank()) {
                when (selectedType) {
                    R.string.business -> {
                        exploreAPIVm.getSearchProperty(
                            user_id = appPrefs.getUserId(),
                            search_text = query.trim()
                        )
                    }

                    R.string.profile -> {
                        exploreAPIVm.searchProfile(
                            user_id = appPrefs.getUserId(),
                            name = query.trim()
                        )
                    }
                }
                mainExpanded = true
            } else {
                mainExpanded = false
            }
        }
    }

    // Dynamic suggestion list based on selected type
    val suggestionCount = when (selectedType) {
        R.string.business -> searchSuggestions.size
        R.string.profile -> profileSearchData.size
        else -> 0
    }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = mainExpanded,
            onExpandedChange = {
                mainExpanded = !mainExpanded
                miniExpanded = false
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    if (it.length <= 30) {
                        exploreUIVm.enterText(it)
                    }
                },
                textStyle = TextStyle(
                    fontSize = textUnit(12),
                    fontFamily = fontFamily(3),
                    color = primaryBlack
                ),
                placeholder = {
                    zText(str(R.string.enter_name), gray66, 12, 2)
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                miniExpanded = true
                                mainExpanded = false
                            }
                        ) {
                            zText(str(selectedType), black1A, 12, 2)
                            spacer(4)
                            Image(
                                painter = painterResource(R.drawable.explore_search_dropdown),
                                contentDescription = "Type dropdown"
                            )
                            spacer(4)
                        }

                        DropdownMenu(
                            expanded = miniExpanded,
                            onDismissRequest = { miniExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 24.dp)
                        ) {
                            typeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = str(option),
                                            color = black1A
                                        )
                                    },
                                    onClick = {
                                        selectedType = option
                                        miniExpanded = false
                                        mainExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = primaryWhite,
                    unfocusedContainerColor = primaryWhite,
                    focusedBorderColor = primaryBlack,
                    unfocusedBorderColor = grayB8,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = mainExpanded && suggestionCount > 0,
                onDismissRequest = { mainExpanded = false },
                containerColor = primaryWhite,
                modifier = Modifier.offset(y = 12.dp)
            ) {
                when (selectedType) {
                    R.string.business -> {
                        searchSuggestions.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item.post_property.title,
                                        color = black1A
                                    )
                                },
                                onClick = {
                                    exploreUIVm.enterText(item.post_property.title)
                                    navigator.navigate(Screen.ExploreSearchPropertyResults(item.post_property.title))
                                    mainExpanded = false
                                }
                            )
                        }
                    }

                    R.string.profile -> {
                        profileSearchData.forEach { profile ->
                            DropdownMenuItem(
                                text = {
                                    ListItem(
                                        leadingContent = {
                                            AsyncImage(model = profile.profile_image , "",
                                                modifier = Modifier.size(32.dp).clip(CircleShape))
                                        },
                                        headlineContent = {
                                            zText(profile.username , gray66 , 14 , 3)
                                        },
                                        overlineContent = {
                                            zText(profile.name , black1A , 14 , 3)
                                        },
                                        colors = ListItemDefaults.colors(
                                            containerColor = primaryWhite
                                        )
                                    )
                                },
                                onClick = {
                                    exploreUIVm.enterText(profile.name ?: "")
                                    mainExpanded = false
                                    navigator.navigate(Screen.OthersProfile(profile.user_id))
                                }
                            )
                        }
                    }
                }

                spacer(4)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDropdownField(
    value: String,
    placeholder: Int,
    items: List<PostUploadGetCategoryResponseData>,
    error: String? = null,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        )
        {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(
                    fontSize = textUnit(12),
                    fontFamily = fontFamily(3),
                    color = primaryBlack
                ),
                placeholder = {
                    zText(str(placeholder), gray66, 12, 2)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = primaryWhite,
                    unfocusedContainerColor = primaryWhite,
                    focusedBorderColor = primaryBlack,
                    unfocusedBorderColor = grayB8,
                    errorBorderColor = redE54
                ),
                isError = error != null,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
                , containerColor = primaryWhite
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            zText(item.category_name ?: item.language_name ?: "", primaryBlack, 12, 2)
                        },
                        onClick = {
                            onItemSelected(item.category_name ?: item.language_name ?: "")
                            expanded = false
                        }
                    )
                }
            }
        }

        spacer(2)

        error?.let {
            zText(it, redE54, 12, 2)
        }
    }
}



@Composable
fun RequiredTitle(title: Int) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = black1A,
                    fontSize = textUnit(16),
                    fontFamily = fontFamily(1)
                )
            ) {
                append(str(title))
            }

            withStyle(style = SpanStyle(color = redE54)) {
                append("*")
            }
        }
    )
}


@Composable
fun UploadTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: Int,
    error: String? = null,
    minHeight: Dp = 56.dp,
    singleLine: Boolean = true
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText(str(placeholder), gray66, 12, 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
                errorBorderColor = redE54
            ),
            isError = error != null,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
        )

        spacer(2)

        error?.let {
            zText(it, redE54, 12, 2)
        }
    }
}



@Composable
fun UY_YoutubeVideo(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText(str(R.string.paste_youtube_link), gray66, 12, 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Uri
            ),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            spacer(2)
            zText(it, redE54, 12, 2)
        }
    }
}



@Composable
fun UA_UploadPhotos(
    images: List<String>,
    error: String? = null,
    onAddClick: () -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(260.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(width = 116.dp, height = 108.dp)
                        .roundedDashedBorder(
                            color = black1A,
                            strokeWidth = 1.dp,
                            dashWidth = 4.dp,
                            dashGap = 4.dp,
                            cornerRadius = 4.dp
                        )
                        .shrinkClick { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.upload_photos),
                        contentDescription = ""
                    )
                }
            }

            items(images) { image ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(width = 116.dp, height = 108.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, black1A, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = image,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .clip(RoundedCornerShape(bottomStart = 8.dp))
                            .background(black1A)
                            .shrinkClick {
                                onRemoveClick(image)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.clear),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(primaryWhite),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        error?.let {
            spacer(2)
            zText(it, redE54, 12, 2)
        }
    }
}



@Composable
fun UploadTextFieldDescs(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    minHeight: Dp = 108.dp,
    singleLine: Boolean = true
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText(placeholder, gray66, 12, 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
                errorBorderColor = redE54
            ),
            isError = error != null,
            //singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
        )

        spacer(2)

        error?.let {
            zText(it, redE54, 12, 2)
        }
    }
}


@Composable
fun UA_SpecificCategory(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder = {
                zText(str(R.string.enter_your_category), gray66, 12, 2)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = primaryWhite,
                unfocusedContainerColor = primaryWhite,
                focusedBorderColor = primaryBlack,
                unfocusedBorderColor = grayB8,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            spacer(2)
            zText(it, redE54, 12, 2)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagesCommonPreview(
    images: List<String>, // String / Uri / File path / url — whatever Coil can load
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState (
        initialPage = 0,
        pageCount = { images.size }
    )

    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            zText(str(R.string.images), black1A, 18, 0)

            Image(
                painter = painterResource(R.drawable.clear),
                contentDescription = ""
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(7f),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 12.dp,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                val isCurrentPage = page == pagerState.currentPage

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                            val scale = lerp(
                                start = 0.92f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(
                                start = 0.65f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(lightBlueF0)
                ) {
                    AsyncImage(
                        model = images[page],
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (!isCurrentPage) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.10f))
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                images.forEachIndexed { index, _ ->
                    val selected = index == pagerState.currentPage

                    if (selected) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .width(20.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(black1A)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(grayB8)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(images) { index, item ->
                    val isSelected = index == pagerState.currentPage

                    Box(
                        modifier = Modifier
                            .size(width = 82.dp, height = 82.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) black1A else grayB8,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = item,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

private fun lerp(
    start: Float,
    stop: Float,
    fraction: Float
): Float {
    return start + (stop - start) * fraction
}


data class MoreOptions(
    var id : Int,
    var title : Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonMoreOptions(){
    var expanded by remember { mutableStateOf(false) }

    val items = listOf(
        MoreOptions(
            id = 0,
            R.string.share
        ),
        MoreOptions(
            id = 1,
            R.string.not_interested
        ),
        MoreOptions(
            id = 2,
            R.string.report
        ),
        MoreOptions(
            id = 3,
            R.string.edit
        ),
        MoreOptions(
            id = 4,
            R.string.delete_post
        ),
    )

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        )
        {

            Image(painter = painterResource(R.drawable.more_vert) , "",
                modifier = Modifier.shrinkClick {
                    expanded = !expanded
                })

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
                , containerColor = primaryWhite
                , modifier = Modifier
                    .width(120.dp)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            zText(str(item.title), primaryBlack, 14, 2)
                        },
                        onClick = {
                            when (item.id){
                                0 -> {
                                    /// share
                                }

                                1 -> {
                                    /// Not interested
                                }

                                2 -> {
                                    /// Report
                                }

                                3 -> {
                                    /// Edit
                                }

                                4 -> {
                                    // delete
                                }
                            }
                            expanded = false
                        }
                        , modifier = Modifier
                            .width(120.dp)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberEmailChangeVerifyBtm(
    number: String = "",
    email : String = "",
    viewModel : CredentialViewModel = koinViewModel()
    ,authApiVm : AuthApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
){

    val btmState by utils.numberemailchangeVerify.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val otp by viewModel.otp.collectAsStateWithLifecycle()

    val otpError by viewModel.otpError.collectAsStateWithLifecycle()

    val timer = rememberCountdownTimer(10)

    val verifyApiState by authApiVm.verifyState.collectAsStateWithLifecycle()


    val network by rememberNetworkStatus()


    if (btmState) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                utils.close_numberemailchangeVerify()
            },
            containerColor = primaryWhite
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                        , horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    zText(str(R.string.verification) , black1A , 24 ,0)

                    Image(painter = painterResource(R.drawable.clear) ,  "",
                        modifier = Modifier.size(24.dp).shrinkClick {
                            viewModel.clearOtp()
                            viewModel.clearOtpError()
                            utils.close_numberemailchangeVerify()
                        })
                }

                zText(str(R.string.number_change_verify_header) , black1A , 14 , 1 )


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
                        .background(lightBlueF0)
                    , verticalAlignment = Alignment.CenterVertically
                    , horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Icon(painter = painterResource(R.drawable.clock_timer) , "")

                    zText(timer.time.value , brandBlue , 14 ,1)
                }

                spacer(8)

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = primaryBlack, fontSize = textUnit(14), fontFamily = fontFamily(2))){
                            append("Didn’t receive ?")
                        }

                        withStyle(style = SpanStyle(color = if (timer.isFinished.value)brandBlue else brandBlue.copy(.5f), fontSize = textUnit(14), fontFamily = fontFamily(2))){
                            append("Resend")
                        }
                    }
                    , modifier = Modifier
                        .shrinkClick {
                            if (network == NetworkStatus.Online) {
                              //  if (authStateFlow) {
                                    // register
                                    authApiVm.register(
                                        name = appPreferences.getUserName(),
                                        phone_num_cc = "+91",
                                        phone_num = number,
                                        device_id = "1234567890",
                                        device_type = "Android",
                                        device_token = ""
                                    )
                                //} else {
                                    // login
                              //  }
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
                                if (verifyApiState is UiState.Loading){

                                }
                                else {
                                    authApiVm.verify(
                                        user_id = appPreferences.getUserId(),
                                        phone_num = number,
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
    }
}