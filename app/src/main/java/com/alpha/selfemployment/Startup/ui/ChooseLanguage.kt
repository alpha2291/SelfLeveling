package com.alpha.selfemployment.Startup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.compose.koinInject

@Composable
fun ChooseLanguageScreen(
    appPrefs: AppPreferences = koinInject(),
    onLanguageChange: (String) -> Unit   // ← receive it here
) {


    val savedLanguage = when(appPrefs.getAppLanguage()) {
        "en" -> "English"
        "ta" -> "Tamil"
        "hi" -> "Hindi"
        else -> "English"
    }

    val selectedLanguage = remember { mutableStateOf(savedLanguage) }

    val navigator = LocalNavigator.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(
                "Choose Language",
                black1A,
                24,
                0,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        LanguageWheelPicker(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(7f),
            initialLanguage = savedLanguage,   // ← pass saved language
            isSelected = { lang -> selectedLanguage.value = lang }
        )

        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(black1A)
                    .shrinkClick {
                        // ✅ fix
                        val code = when (selectedLanguage.value) {
                            "English" -> "en"
                            "Tamil"   -> "ta"
                            "Hindi"   -> "hi"
                            else      -> "en"
                        }
                        appPrefs.save_AppLanguage(code)
                        onLanguageChange(code)
                        appPrefs.saveLanguageCompleted(true)
                        navigator.clearAndNavigate(Screen.LaunchScreen)
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Get Started", primaryWhite, 14, 0)
            }
        }
    }
}

@Composable
fun LanguageWheelPicker(
    modifier: Modifier = Modifier,
    initialLanguage: String = "English",   // ← add this
    isSelected: (String) -> Unit
) {
    val languages = listOf("Tamil", "English", "Hindi")
    val itemHeight = 60.dp

    val pagerState = rememberPagerState(
        initialPage = languages.indexOf(initialLanguage).takeIf { it >= 0 } ?: 1,
        pageCount = { languages.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        isSelected(languages[pagerState.currentPage])
    }
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val verticalPadding = (maxHeight - itemHeight) / 2

        Box(
            modifier = Modifier
                .height(itemHeight)
                .fillMaxWidth()
                .background(lightGreenEBF, RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    Color(0xFF2E7D32),
                    RoundedCornerShape(8.dp)
                )
        )

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                vertical = verticalPadding
            )
        ) { page ->

            val selected = page == pagerState.currentPage

            Text(
                text = languages[page],
                fontSize = if (selected) 20.sp else 16.sp,
                color = if (selected) Color(0xFF2E7D32) else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}