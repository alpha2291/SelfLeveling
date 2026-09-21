package com.alpha.selfemployment.Views.PostUpload.ui.EditFlow

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.R
import com.alpha.selfemployment.RequiredTitle
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UA_SpecificCategory
import com.alpha.selfemployment.UA_UploadPhotos
import com.alpha.selfemployment.UploadDropdownField
import com.alpha.selfemployment.UploadTextField
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import kotlin.text.equals

@Composable
fun UploadArticleDetails(
    uiVm: PostUploadUIViewModel = koinViewModel(),
    postUploadApiVm: PostUploadAPIViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.current

    val articleFields by uiVm.articlePostFields.collectAsStateWithLifecycle()
    val articleErrors by uiVm.articlePostErrors.collectAsStateWithLifecycle()


    val isFormValid = remember(articleFields) {
        uiVm.isArticleFormValid()
    }

    LaunchedEffect(Unit) {
        postUploadApiVm.getCategories { }
    }

    // Replace these with your actual api lists
    val categoryItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }


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

    }


    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri ?: return@rememberLauncherForActivityResult

            uiVm.addArticleImage(uri)

            // show image instantly (local preview)
            // profileVm.update_bitmapImage(uri)

            // upload to S3
            // viewModel.uploadProfileImage(uri)


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
                "Upload Article",
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
        ) {
            item {
                RequiredTitle(R.string.category)
                spacer(2)

                UploadDropdownField(
                    value = articleFields.category,
                    placeholder = R.string.select_category,
                    items = categoryItems,
                    error = articleErrors.category,
                    onItemSelected = uiVm::updateArticleCategory
                )

                if (articleFields.category.equals("Others", ignoreCase = true)) {
                    spacer(8)

                    RequiredTitle(R.string.specific_category)
                    spacer(2)

                    UA_SpecificCategory(
                        value = articleFields.specificCategory,
                        error = articleErrors.specificCategory,
                        onValueChange = uiVm::updateArticleSpecificCategory
                    )
                }

                spacer(8)

                RequiredTitle(R.string.title)
                spacer(2)

                UploadTextField(
                    value = articleFields.title,
                    onValueChange = uiVm::updateArticleTitle,
                    placeholder = R.string.give_a_title,
                    error = articleErrors.title
                )

                spacer(8)

                RequiredTitle(R.string.upload_images)
                spacer(2)

                UA_UploadPhotos(
                    images = articleFields.images,
                    error = articleErrors.images,
                    onAddClick = {
                        imagePickerLauncher.launch(arrayOf("image/*"))
                    },
                    onRemoveClick = { image ->
                        uiVm.removeArticleImage(image)
                    }
                )

                spacer(8)

                RequiredTitle(R.string.short_description)
                spacer(2)

                UploadTextField(
                    value = articleFields.shortDesc,
                    onValueChange = uiVm::updateArticleShortDesc,
                    placeholder = R.string.start_typing_here,
                    error = articleErrors.shortDesc,
                    minHeight = 112.dp,
                    singleLine = false
                )

                spacer(8)

                RequiredTitle(R.string.long_description)
                spacer(2)

                UploadTextField(
                    value = articleFields.longDesc,
                    onValueChange = uiVm::updateArticleLongDesc,
                    placeholder = R.string.start_typing_here,
                    error = articleErrors.longDesc,
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
                        if (uiVm.validateArticleForm()) {
                            navigator.navigate(Screen.ArticleUploadPreview)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Preview", primaryWhite, 16, 0)
            }
        }
    }
}
