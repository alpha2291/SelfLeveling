package com.alpha.selfemployment.Views.PostUpload.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import coil.compose.AsyncImage
import com.alpha.selfemployment.R
import com.alpha.selfemployment.RequiredTitle
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UA_SpecificCategory
import com.alpha.selfemployment.UA_UploadPhotos
import com.alpha.selfemployment.UploadDropdownField
import com.alpha.selfemployment.UploadTextField
import com.alpha.selfemployment.Views.PostUpload.data.network.PostUploadAPI
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.roundedDashedBorder
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue


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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleUploadPreview(
    uiVm: PostUploadUIViewModel = koinViewModel(),
) {
    val navigator = LocalNavigator.current
    val articleFields by uiVm.articlePostFields.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { articleFields.images.size }
    )

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

            zText("Preview", black1A, 24, 0)
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(8f)
                .clip(RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    zText(
                        articleFields.category,
                        green3A8,
                        12,
                        0,
                        modifier = Modifier
                            .background(lightGreenEBF, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    spacer(8)
                    zText(articleFields.title, gray48, 18, 0)
                    spacer(12)
                }

                if (articleFields.images.isNotEmpty()) {
                    item {
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 36.dp),
                            pageSpacing = 12.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) { page ->
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    ).absoluteValue

                            val scale = lerp(
                                start = 0.86f,
                                stop = 1f,
                                fraction = (1f - pageOffset.coerceIn(0f, 1f))
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = lerp(
                                            start = 0.65f,
                                            stop = 1f,
                                            fraction = (1f - pageOffset.coerceIn(0f, 1f))
                                        )
                                    }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(lightGreenEBF),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = articleFields.images[page],
                                    contentDescription = "Article image ${page + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(articleFields.images.size) { index ->
                                val selected = pagerState.currentPage == index

                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(6.dp)
                                        .width(if (selected) 20.dp else 6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (selected) black1A else grayB8)
                                )
                            }
                        }

                        spacer(8)
                    }
                }

                item {
                    Column {
                        spacer(8)
                        zText("Short Description", gray48, 16, 0)
                        spacer(4)
                        androidx.compose.material.Text(
                            text = articleFields.shortDesc,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(16)
                    }
                }

                item {
                    Column {
                        spacer(8)
                        zText("Long Description", gray48, 16, 0)
                        spacer(4)
                        androidx.compose.material.Text(
                            text = articleFields.longDesc,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(16)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()

            spacer(4)

            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(black1A)
                    .shrinkClick {
                        // post action
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Post now", primaryWhite, 16, 0)
            }
        }
    }
}
