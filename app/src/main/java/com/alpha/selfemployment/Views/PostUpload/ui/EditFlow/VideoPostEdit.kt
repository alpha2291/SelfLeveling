package com.alpha.selfemployment.Views.PostUpload.ui.EditFlow

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.R
import com.alpha.selfemployment.RequiredTitle
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UA_SpecificCategory
import com.alpha.selfemployment.UploadDropdownField
import com.alpha.selfemployment.UploadTextField
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.createVideoThumbnail
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import kotlin.text.equals


@Composable
fun VideoPostEdit(
    videoUri: Uri?,
    uiVm: PostUploadUIViewModel = koinViewModel(),
    postUploadApiVm: PostUploadAPIViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val fields by uiVm.videoPostFields.collectAsStateWithLifecycle()
    val errors by uiVm.videoPostErrors.collectAsStateWithLifecycle()


    val isFormValid = remember(fields) {
        uiVm.isVideoFormValid()
    }

    LaunchedEffect(videoUri) {
        uiVm.setVideoUri(videoUri)
    }


    // Replace these with your actual api lists
    val categoryItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }
    val languageItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }


    LaunchedEffect(Unit) {
        postUploadApiVm.getCategories {
                result ->
            when(result) {
                is ResultHandler.Success -> {
                    categoryItems.clear()
                    categoryItems.addAll(result.data.data)
                }
                is ResultHandler.Error -> {

                }
                else -> {}
            }
        }
        postUploadApiVm.get_Language {
                result ->
            when(result) {
                is ResultHandler.Success -> {
                    languageItems.clear()
                    languageItems.addAll(result.data.data)
                }
                is ResultHandler.Error -> {}
                else -> {}
            }
        }
    }

//    val languageItems = listOf("English", "Hindi", "Tamil", "Telugu")

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.left_arrow),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick { navigator.pop() }
            )

            zText(
                "Upload Video",
                primaryBlack,
                24,
                0,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f)
                .padding(horizontal = 16.dp)
        )
        {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.7f)
                            .height(360.dp)
                            .background(lightGreenEBF),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumbnail = remember(videoUri) {
                            videoUri?.let { createVideoThumbnail(context, it) }
                        }

                        SubcomposeAsyncImage(
                            model = thumbnail,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                spacer(8)
            }

            item {
                RequiredTitle(R.string.category)
                spacer(2)

                UploadDropdownField(
                    value = fields.category,
                    placeholder = R.string.select_category,
                    items = categoryItems,
                    error = errors.category,
                    onItemSelected = uiVm::updateVideoCategory
                )


                if (fields.category.equals("Others", ignoreCase = true)) {
                    spacer(8)

                    RequiredTitle(R.string.specific_category)
                    spacer(2)

                    UA_SpecificCategory(
                        value = fields.specificCategory,
                        error = fields.specificCategory,
                        onValueChange = uiVm::updateVideoSpecificCategory
                    )
                }

                spacer(8)

                RequiredTitle(R.string.title)
                spacer(2)

                UploadTextField(
                    value = fields.title,
                    onValueChange = uiVm::updateVideoTitle,
                    placeholder = R.string.give_a_title,
                    error = errors.title
                )

                spacer(8)

                RequiredTitle(R.string.language)
                spacer(2)

                zText(
                    str(R.string.language_sub_header),
                    gray66,
                    12,
                    2
                )

                spacer(2)

                UploadDropdownField(
                    value = fields.language,
                    placeholder = R.string.select_language,
                    items = languageItems,
                    error = errors.language,
                    onItemSelected = uiVm::updateVideoLanguage
                )

                spacer(8)

                RequiredTitle(R.string.short_description)
                spacer(2)

                UploadTextField(
                    value = fields.shortDesc,
                    onValueChange = uiVm::updateVideoShortDesc,
                    placeholder = R.string.start_typing_here,
                    error = errors.shortDesc,
                    minHeight = 112.dp,
                    singleLine = false
                )

                spacer(8)

                RequiredTitle(R.string.long_description)
                spacer(2)

                UploadTextField(
                    value = fields.longDesc,
                    onValueChange = uiVm::updateVideoLongDesc,
                    placeholder = R.string.start_typing_here,
                    error = errors.longDesc,
                    minHeight = 240.dp,
                    singleLine = false
                )

                spacer(16)
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , verticalArrangement = Arrangement.Center
            , horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            HorizontalDivider()

            spacer(4)

            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFormValid) black1A else black1A.copy(.4f))
                    .shrinkClick {
                        if (uiVm.validateVideoForm()) {
                            navigator.navigate(Screen.VideoUploadPreview(videoUri))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Preview", primaryWhite, 16, 0)
            }
        }
    }
}