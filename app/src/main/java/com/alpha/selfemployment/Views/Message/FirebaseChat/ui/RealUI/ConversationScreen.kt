package com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.Message.FirebaseChat.di.ConversationViewModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.models.MessageModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// COLOR TOKENS
// ─────────────────────────────────────────────────────────────────────────────
private val SentBubble     = Color(0xFF1C1C1E)
private val ReceivedBubble = Color(0xFFF2F2F7)
private val SentText       = Color.White
private val ReceivedText   = Color(0xFF1C1C1E)
private val HeaderBg       = Color.White
private val ScreenBg       = Color(0xFFF7F7F9)
private val SendButton     = Color(0xFF1C1C1E)
private val TimeColor      = Color(0xFF8E8E93)
private val DividerText    = Color(0xFF8E8E93)
private val InputBorder    = Color(0xFFE5E5EA)

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chatId:        String,
    postId:        String,
    currentUserId: String,
    otherUserId:   String,
    otherUserName: String,
    onBack:        () -> Unit = {},
    // IMPORTANT: pass a unique key tied to chatId so Koin creates a fresh
    // ViewModel per conversation instead of reusing one across chats.
    viewModel: ConversationViewModel = koinViewModel(key = chatId)
) {
    val uiState   by viewModel.uiState.collectAsState()
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()
    var inputText  by remember { mutableStateOf("") }

    // Start observing — safe to call multiple times (VM guards against re-subscribe)
    LaunchedEffect(chatId) {
        viewModel.observeMessages(chatId, currentUserId)
    }

    // Auto-scroll to latest when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    // Mark as read ONLY after the list has scrolled to the bottom
    // (i.e. the user has actually seen the messages).
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty() && !uiState.isLoading) {
            viewModel.markRead(chatId, currentUserId)
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            ChatTopBar(
                otherUserName = otherUserName,
                onBack        = onBack
            )
        },
        bottomBar = {
            MessageInputBar(
                text      = inputText,
                isSending = uiState.isSending,
                onChange  = { inputText = it },
                onSend    = {
                    val trimmed = inputText.trim()
                    if (trimmed.isNotBlank()) {
                        viewModel.sendMessage(
                            chatId     = chatId,
                            postId     = postId,
                            senderId   = currentUserId,
                            receiverId = otherUserId,
                            text       = trimmed
                        )
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1C1C1E))
                }
            }

            uiState.messages.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "No messages yet. Say hello!",
                        color     = TimeColor,
                        fontSize  = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val grouped = remember(uiState.messages) {
                    groupMessagesByDate(uiState.messages)
                }

                LazyColumn(
                    state               = listState,
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    contentPadding      = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    grouped.forEach { (label, messages) ->

                        item(key = "sep_$label") {
                            DateSeparator(label = label)
                        }

                        items(messages, key = { it.messageId }) { msg ->
                            // Hide messages deleted for this user
                            val deletedForMe = msg.deletedFor[currentUserId] == true
                            if (!deletedForMe) {
                                MessageBubble(
                                    message   = msg,
                                    isMine    = msg.senderId == currentUserId,
                                    otherName = otherUserName
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show error snackbar if needed
        uiState.error?.let { errorMsg ->
            LaunchedEffect(errorMsg) {
                // You can show a Snackbar here via a SnackbarHostState if desired
                android.util.Log.e("ConversationScreen", "Error: $errorMsg")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    otherUserName: String,
    onBack: () -> Unit
) {
    Surface(shadowElevation = 2.dp, color = HeaderBg) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Image(painter = painterResource(R.drawable.left_arrow), contentDescription = "Back")
            }

            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1D1D6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = otherUserName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF3C3C43),
                    fontSize   = 16.sp
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = otherUserName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = Color(0xFF1C1C1E)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DATE SEPARATOR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DateSeparator(label: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E5EA), thickness = 0.8.dp)
        Text(
            text       = label,
            fontSize   = 12.sp,
            color      = DividerText,
            modifier   = Modifier.padding(horizontal = 12.dp),
            fontWeight = FontWeight.Medium
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E5EA), thickness = 0.8.dp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MESSAGE BUBBLE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MessageBubble(
    message:   MessageModel,
    isMine:    Boolean,
    otherName: String
) {
    val bubbleBg  = if (isMine) SentBubble   else ReceivedBubble
    val textColor = if (isMine) SentText     else ReceivedText
    val alignment = if (isMine) Arrangement.End else Arrangement.Start

    val timeStr = remember(message.timestamp) {
        if (message.timestamp > 0)
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
        else ""
    }

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = alignment,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (!isMine) {
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1D1D6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = otherName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF3C3C43)
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            modifier            = Modifier.widthIn(max = 270.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (isMine) 18.dp else 4.dp,
                            bottomEnd   = if (isMine) 4.dp  else 18.dp
                        )
                    )
                    .background(bubbleBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = message.text, color = textColor, fontSize = 15.sp, lineHeight = 21.sp)
            }

            if (timeStr.isNotEmpty()) {
                Spacer(Modifier.height(3.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {
                    Text(text = timeStr, fontSize = 11.sp, color = TimeColor)
                    if (isMine) {
                        Spacer(Modifier.width(4.dp))
                        Text(text = "✓✓", fontSize = 11.sp, color = TimeColor)
                    }
                }
            }
        }

        if (isMine) Spacer(Modifier.width(4.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MESSAGE INPUT BAR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MessageInputBar(
    text:      String,
    isSending: Boolean,
    onChange:  (String) -> Unit,
    onSend:    () -> Unit
) {
    Surface(color = HeaderBg, shadowElevation = 4.dp) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value         = text,
                onValueChange = onChange,
                modifier      = Modifier.weight(1f),
                placeholder   = {
                    Text("Type message here...", color = Color(0xFFAEAEB2), fontSize = 15.sp)
                },
                shape         = RoundedCornerShape(24.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = InputBorder,
                    focusedBorderColor   = Color(0xFF8E8E93),
                    cursorColor          = Color(0xFF1C1C1E)
                ),
                singleLine      = false,
                maxLines        = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                textStyle       = LocalTextStyle.current.copy(fontSize = 15.sp)
            )

            AnimatedContent(
                targetState   = isSending,
                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                label         = "send_anim"
            ) { sending ->
                Box(
                    modifier         = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (text.isBlank()) Color(0xFFD1D1D6) else SendButton),
                    contentAlignment = Alignment.Center
                ) {
                    if (sending) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onSend, enabled = text.isNotBlank()) {
                            // Replace with your send icon resource
                            // Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = if (text.isBlank()) Color(0xFF8E8E93) else Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DATE GROUPING HELPER
// ─────────────────────────────────────────────────────────────────────────────
private fun groupMessagesByDate(
    messages: List<MessageModel>
): LinkedHashMap<String, List<MessageModel>> {
    val result    = LinkedHashMap<String, List<MessageModel>>()
    val today     = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val fmt       = SimpleDateFormat("d MMM", Locale.getDefault())

    messages.forEach { msg ->
        val cal = Calendar.getInstance().apply {
            timeInMillis = if (msg.timestamp > 0) msg.timestamp else System.currentTimeMillis()
        }
        val label = when {
            isSameDay(cal, today)     -> "Today"
            isSameDay(cal, yesterday) -> "Yesterday"
            else                      -> fmt.format(cal.time)
        }
        result[label] = (result[label] ?: emptyList()) + msg
    }
    return result
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR)        == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)