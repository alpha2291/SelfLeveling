package com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.Views.Message.BackendSupport.di.ChatBackEndViewModel
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.ChatBEListResponseData
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository.ChatRepository
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// COLORS
// ─────────────────────────────────────────────────────────────────────────────
private val Black         = Color(0xFF0D0D0D)
private val White         = Color.White
private val ScreenBg      = Color(0xFFF7F7F9)
private val CardBg        = Color.White
private val TabSelected   = Black
private val TabUnselected = Color(0xFFF0F0F0)
private val SubtitleGray  = Color(0xFF8E8E93)
private val BadgeBg       = Color(0xFF22C55E)
private val BorderColor   = Color(0xFFE5E5EA)

// ─────────────────────────────────────────────────────────────────────────────
// TABS
// ─────────────────────────────────────────────────────────────────────────────
private val TABS = listOf("All", "Received", "Sent")

/**
 *   chat_type from API:  1 = Received (post owner),  2 = Sent (enquirer)
 */


private fun ChatBEListResponseData.matchesTab(tab: String) = when (tab) {
    "All"      -> true
    "Received" -> chat_type == 1
    "Sent"     -> chat_type == 2
    else       -> true
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MessagesScreen(
    viewModel      : ChatBackEndViewModel = koinViewModel(),
    appPrefs       : AppPreferences       = koinInject(),
    sharedRepo     : SharedRepository     = koinInject(),
    chatRepository : ChatRepository       = koinInject()
) {
    val navigator = LocalNavigator.current
    val scope     = rememberCoroutineScope()

    val chatList  by sharedRepo.chatList.collectAsState()
    val isLoading by sharedRepo.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf("All") }
    val listState   = rememberLazyListState()

    // Load data whenever the selected tab changes (and on first composition).
    // Using `selectedTab` as the key means a tab switch always triggers a fresh load.
    LaunchedEffect(selectedTab) {
        viewModel.chat_list(
            user_id     = appPrefs.getUserId(),
            filter_type = when (selectedTab) {
                "All"      -> "1,2"
                "Received" -> "1"
                else       -> "2"
            },
            loadMore = false   // fresh load on tab change
        )
    }

    // Pagination: load more when user nears the bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total       = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 2
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.chat_list(
                user_id     = appPrefs.getUserId(),
                filter_type = when (selectedTab) {
                    "All"      -> "1,2"
                    "Received" -> "1"
                    else       -> "2"
                },
                loadMore = true
            )
        }
    }

    // Client-side filter so the list updates instantly when switching tabs
    // (server response may still be in-flight)
    val filtered = remember(chatList, selectedTab) {
        chatList.filter { it.matchesTab(selectedTab) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        // ── Header ───────────────────────────────────────────────
        Surface(color = White, shadowElevation = 2.dp) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Messages",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Black,
                    modifier   = Modifier.weight(1f),
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.width(48.dp))
            }
        }

        // ── Tab Row ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TabUnselected)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TABS.forEach { tab ->
                val selected = tab == selectedTab
                // Badge count = unread Received items only
                val badgeCount = if (tab == "Received") {
                    chatList.count { it.chat_type == 1 }
                } else 0

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) TabSelected else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text       = tab,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = if (selected) White else Black
                        )
                        if (tab == "Received" && badgeCount > 0) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier         = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BadgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = badgeCount.toString(),
                                    fontSize   = 11.sp,
                                    color      = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── List / Empty / Loading ────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {

            if (isLoading && filtered.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = Black
                )
            } else if (!isLoading && filtered.isEmpty()) {
                Text(
                    text     = "No messages yet",
                    modifier = Modifier.align(Alignment.Center),
                    color    = SubtitleGray,
                    fontSize = 15.sp
                )
            } else {
                LazyColumn(
                    state               = listState,
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filtered,
                        key   = { it.video_model.user_post_id }
                    ) { item ->

                        if (item.chat_type == 1) {
                            // RECEIVED — owner taps to see list of users who enquired
                            ReceivedChatCard(
                                item    = item,
                                onClick = {
                                    navigator.navigate(
                                        Screen.PostChatUsers(
                                            postId = item.video_model.user_post_id.toString()
                                        )
                                    )
                                }
                            )
                        } else {
                            // SENT — enquirer taps to open the single conversation
                            SentChatCard(
                                item    = item,
                                onClick = {
                                    scope.launch {
                                        val post  = item.video_model
                                        val owner = item.user_details.firstOrNull()

                                        val chatId = chatRepository.createChatAndSendDefaultMessage(
                                            postId                = post.user_post_id.toString(),
                                            ownerId               = post.user_id.toString(),
                                            ownerName             = post.name,
                                            ownerUsername         = post.component11(), // username field
                                            ownerPhone            = "",
                                            ownerProfileImage     = "",
                                            recipientId           = appPrefs.getUserId().toString(),
                                            recipientName         = appPrefs.getRealName(),
                                            recipientUsername     = appPrefs.getUserName(),
                                            recipientPhone        = appPrefs.getPhoneNumber(),
                                            recipientProfileImage = "",
                                            defaultMessage        = "Hello, I'm interested in your post."
                                        )

                                        navigator.navigate(
                                            Screen.Conversation(
                                                chatId        = chatId,
                                                postId        = post.user_post_id.toString(),
                                                currentUserId = appPrefs.getUserId().toString(),
                                                otherUserId   = post.user_id.toString(),
                                                otherUserName = owner?.name?.ifBlank { owner.username }
                                                    ?: "Owner"
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // Bottom pagination loader
                    if (isLoading && filtered.isNotEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(24.dp),
                                    color       = Black,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// RECEIVED CARD  (owner — multiple enquirers → "View messages")
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ReceivedChatCard(
    item   : ChatBEListResponseData,
    onClick: () -> Unit
) {
    val post  = item.video_model
    val users = item.user_details

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            PostThumbnail(imageUrl = post.post_property.video.ifBlank { "" })
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = post.post_property.category.ifBlank { "Post" },
                    fontSize   = 12.sp,
                    color      = SubtitleGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text       = post.post_property.title,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Black,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "Replies", fontSize = 13.sp, color = SubtitleGray)
                Spacer(Modifier.height(6.dp))
                StackedAvatars(users = users)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick  = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Black)
                ) {
                    Text("View messages", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SENT CARD  (enquirer — single owner → "View message")
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SentChatCard(
    item   : ChatBEListResponseData,
    onClick: () -> Unit
) {
    val post  = item.video_model
    val owner = item.user_details.firstOrNull()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            PostThumbnail(imageUrl = post.post_property.video.ifBlank { "" })
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = post.post_property.category.ifBlank { "Post" },
                    fontSize   = 12.sp,
                    color      = SubtitleGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text       = post.post_property.title,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Black,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "Sent to", fontSize = 13.sp, color = SubtitleGray)
                Spacer(Modifier.height(6.dp))
                if (owner != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            imageUrl = owner.profile_image,
                            name     = owner.name.ifBlank { owner.username },
                            size     = 28
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = owner.username.ifBlank { owner.name },
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Black
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick  = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Black)
                ) {
                    Text("View message", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED COMPOSABLES
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostThumbnail(imageUrl: String) {
    Box(
        modifier = Modifier
            .size(width = 110.dp, height = 130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD1D1D6))
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model              = imageUrl,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun StackedAvatars(
    users: List<com.alpha.selfemployment.Views.Message.BackendSupport.domain.model.UserDetail>
) {
    val visible = users.take(4)
    val extra   = users.size - visible.size

    Row {
        visible.forEachIndexed { index, user ->
            Box(
                modifier = Modifier
                    .offset(x = (-index * 10).dp)
                    .zIndex((visible.size - index).toFloat())
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, White, CircleShape)
            ) {
                UserAvatar(
                    imageUrl = user.profile_image,
                    name     = user.name.ifBlank { user.username },
                    size     = 34
                )
            }
        }
        if (extra > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-(visible.size) * 10).dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, White, CircleShape)
                    .background(Color(0xFF3C3C43)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "+$extra",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = White
                )
            }
        }
    }
}

@Composable
fun UserAvatar(imageUrl: String, name: String, size: Int) {
    Box(
        modifier         = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFD1D1D6)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model              = imageUrl,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text       = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize   = (size * 0.35).sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF3C3C43)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return try {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    } catch (e: Exception) { "" }
}