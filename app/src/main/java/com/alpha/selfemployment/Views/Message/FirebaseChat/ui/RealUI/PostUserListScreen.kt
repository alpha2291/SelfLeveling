package com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.Views.Message.FirebaseChat.di.PostChatUsersViewModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.PostChatUserItem
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Black        = Color(0xFF0D0D0D)
private val White        = Color.White
private val ScreenBg     = Color(0xFFF7F7F9)
private val CardBg       = Color.White
private val SubtitleGray = Color(0xFF8E8E93)
private val OnlineGreen  = Color(0xFF22C55E)
private val BadgeRed     = Color(0xFFEF4444)

@Composable
fun PostChatUsersScreen(
    postId    : String,
    viewModel : PostChatUsersViewModel = koinViewModel(),
    appPrefs  : AppPreferences         = koinInject()
) {
    val navigator = LocalNavigator.current
    val users     by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(postId) {
        viewModel.observePostUsers(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        // ── Header ─────────────────────────────────────────────────
        Surface(color = White, shadowElevation = 2.dp) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator.pop() }) {
                    // Replace with your back-arrow drawable:
                    // Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Black)
                }
                Text(
                    text       = "Interested Users",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Black,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(48.dp))
            }
        }

        // ── Body ───────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color    = Black
                    )
                }

                users.isEmpty() -> {
                    Column(
                        modifier            = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = "No users yet",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Black
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "People who message about this post will appear here.",
                            fontSize = 14.sp,
                            color    = SubtitleGray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(users, key = { it.chatId }) { user ->
                            PostChatUserCard(
                                item    = user,
                                onClick = {
                                    navigator.navigate(
                                        Screen.Conversation(
                                            chatId        = user.chatId,
                                            postId        = postId,
                                            currentUserId = appPrefs.getUserId().toString(),
                                            otherUserId   = user.userId,
                                            otherUserName = user.name.ifBlank { user.username }
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostChatUserCard(
    item   : PostChatUserItem,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar + online dot
            Box {
                UserAvatar(
                    imageUrl = item.profileImage,
                    name     = item.name.ifBlank { item.username },
                    size     = 56
                )
                if (item.isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(OnlineGreen)
                            .border(2.dp, White, CircleShape)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name + time
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text       = item.name.ifBlank { item.username.ifBlank { "User" } },
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Black,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f)
                    )
                    Text(
                        text     = formatTimestamp(item.lastMessageAt),
                        fontSize = 12.sp,
                        color    = SubtitleGray
                    )
                }

                Spacer(Modifier.height(2.dp))

                // @username · phone
                Text(
                    text     = buildString {
                        if (item.username.isNotBlank()) append("@${item.username}")
                        if (item.phone.isNotBlank()) {
                            if (isNotEmpty()) append(" · ")
                            append(item.phone)
                        }
                    }.ifBlank { "No details available" },
                    fontSize = 13.sp,
                    color    = SubtitleGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // Last message + unread badge
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text     = item.lastMessage.ifBlank { "No messages yet" },
                        fontSize = 14.sp,
                        color    = if (item.unreadCount > 0) Black else SubtitleGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (item.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                    )

                    if (item.unreadCount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Badge(
                            containerColor = BadgeRed,
                            contentColor   = White
                        ) {
                            Text(
                                text       = if (item.unreadCount > 99) "99+" else item.unreadCount.toString(),
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return try {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    } catch (e: Exception) { "" }
}