package com.alpha.selfemployment.Views.PostUpload.ui.EditFlow

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.R
import com.alpha.selfemployment.RequiredTitle
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UA_SpecificCategory
import com.alpha.selfemployment.UY_YoutubeVideo
import com.alpha.selfemployment.UploadDropdownField
import com.alpha.selfemployment.UploadTextField
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.extractYoutubeId
import com.alpha.selfemployment.getYoutubeThumbnail
import com.alpha.selfemployment.navigation.LocalNavigator
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
fun YoutubeEditScreen(
    uiVm: PostUploadUIViewModel = koinViewModel(),
    postUploadApiVm: PostUploadAPIViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.current

    val youtubeFields by uiVm.youtubePostFields.collectAsStateWithLifecycle()
    val youtubeErrors by uiVm.youtubePostErrors.collectAsStateWithLifecycle()


    // Replace these with your actual api lists
    val categoryItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }
    val languageItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }



    val isFormValid = remember(youtubeFields) {
        uiVm.isYoutubeFormValid()
    }


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



    var thumbnail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(youtubeFields.youtubeLink) {
        val id = extractYoutubeId(youtubeFields.youtubeLink)
        if (id != null) {
            thumbnail = getYoutubeThumbnail(id)
        }

    }


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
                "Upload YouTube Video",
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
                    value = youtubeFields.category,
                    placeholder = R.string.select_category,
                    items = categoryItems,
                    error = youtubeErrors.category,
                    onItemSelected = uiVm::updateYoutubeCategory
                )

                if (youtubeFields.category.equals("Others", ignoreCase = true)) {
                    spacer(8)

                    RequiredTitle(R.string.specific_category)
                    spacer(2)

                    UA_SpecificCategory(
                        value = youtubeFields.specificCategory,
                        error = youtubeErrors.specificCategory,
                        onValueChange = uiVm::updateYoutubeSpecificCategory
                    )
                }

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
                    value = youtubeFields.language,
                    placeholder = R.string.select_language,
                    items = languageItems,
                    error = youtubeErrors.language,
                    onItemSelected = uiVm::updateYoutubeLanguage
                )

                spacer(8)

                RequiredTitle(R.string.add_youtube_link)
                spacer(2)

                UY_YoutubeVideo(
                    value = youtubeFields.youtubeLink,
                    error = youtubeErrors.youtubeLink,
                    onValueChange = uiVm::updateYoutubeLink
                )

                spacer(8)

                RequiredTitle(R.string.title)
                spacer(2)

                UploadTextField(
                    value = youtubeFields.title,
                    onValueChange = uiVm::updateYoutubeTitle,
                    placeholder = R.string.give_a_title,
                    error = youtubeErrors.title
                )


                spacer(8)

                RequiredTitle(R.string.short_description)
                spacer(2)

                UploadTextField(
                    value = youtubeFields.shortDesc,
                    onValueChange = uiVm::updateYoutubeShortDesc,
                    placeholder = R.string.start_typing_here,
                    error = youtubeErrors.shortDesc,
                    minHeight = 112.dp,
                    singleLine = false
                )

                spacer(8)

                RequiredTitle(R.string.long_description)
                spacer(2)

                UploadTextField(
                    value = youtubeFields.longDesc,
                    onValueChange = uiVm::updateYoutubeLongDesc,
                    placeholder = R.string.start_typing_here,
                    error = youtubeErrors.longDesc,
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
                        if (uiVm.validateYoutubeForm()) {
                            // navigator.navigate(Screen.ArticleUploadPreview)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Post now", primaryWhite, 16, 0)
            }
        }
    }
}