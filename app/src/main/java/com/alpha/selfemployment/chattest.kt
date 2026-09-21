package com.alpha.selfemployment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Shape builders ───────────────────────────────────────────────────────────

/**
 * Received bubble (tail on bottom-left).
 * The bubble itself has rounded corners except the bottom-left,
 * and a triangular tail is drawn there.
 */
fun receivedBubbleShape(
    cornerRadius: Float = 48f,
    tailWidth: Float = 24f,
    tailHeight: Float = 28f,
): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val r = cornerRadius

    // Start just after top-left corner
    moveTo(r, 0f)

    // Top edge → top-right corner
    lineTo(w - r, 0f)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(w - 2 * r, 0f, w, 2 * r),
        startAngleDegrees = -90f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    // Right edge → bottom-right corner
    lineTo(w, h - r)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(w - 2 * r, h - 2 * r, w, h),
        startAngleDegrees = 0f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    // Bottom edge → tail start (leaves gap for tail)
    lineTo(tailWidth, h)

    // Tail triangle pointing down-left
    lineTo(0f, h + tailHeight)
    lineTo(0f, h - r)

    // Left edge → top-left corner
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, 2 * r, 2 * r),
        startAngleDegrees = 180f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    close()
}

/**
 * Sent bubble (tail on bottom-right).
 */
fun sentBubbleShape(
    cornerRadius: Float = 48f,
    tailWidth: Float = 24f,
    tailHeight: Float = 28f,
): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val r = cornerRadius

    // Start after top-left corner
    moveTo(r, 0f)

    // Top edge → top-right corner
    lineTo(w - r, 0f)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(w - 2 * r, 0f, w, 2 * r),
        startAngleDegrees = -90f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    // Right edge down to tail start
    lineTo(w, h - r)

    // Tail triangle pointing down-right
    lineTo(w, h - r)
    lineTo(w + tailWidth, h + tailHeight)  // tip of tail
    lineTo(w, h)

    // Bottom edge ← from right (tail side) to bottom-left corner
    lineTo(r, h)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, h - 2 * r, 2 * r, h),
        startAngleDegrees = 90f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    // Left edge → top-left corner
    lineTo(0f, r)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, 2 * r, 2 * r),
        startAngleDegrees = 180f, sweepAngleDegrees = 90f, forceMoveTo = false
    )

    close()
}

// ─── Bubble composables ───────────────────────────────────────────────────────

@Composable
fun ReceivedBubble(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFE9E9EB),
    textColor: Color = Color(0xFF1C1C1E),
    tailWidth: Float = 24f,
    tailHeight: Float = 28f,
    cornerRadius: Float = 48f,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Extra start padding = tail width so bubble doesn't clip
        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 64.dp, top = 2.dp, bottom = 2.dp)
                .background(
                    color = backgroundColor,
                    shape = receivedBubbleShape(cornerRadius, tailWidth, tailHeight)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            )
        }
    }
}

@Composable
fun SentBubble(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF0A84FF),
    textColor: Color = Color.White,
    tailWidth: Float = 24f,
    tailHeight: Float = 28f,
    cornerRadius: Float = 48f,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        // Extra end padding = tail width so tail isn't clipped
        Box(
            modifier = Modifier
                .padding(start = 64.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
                .background(
                    color = backgroundColor,
                    shape = sentBubbleShape(cornerRadius, tailWidth, tailHeight)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            )
        }
    }
}

// ─── Demo screen ──────────────────────────────────────────────────────────────

data class ChatMessage(val text: String, val isSent: Boolean)

@Composable
fun ChatScreen() {
    val messages = listOf(
        ChatMessage("Hello Demola, What job role is this document for?", isSent = false),
        ChatMessage(
            "Technical Project Manager role.\nHybrid, 3 days on site a week.",
            isSent = true
        ),
        ChatMessage("Got it! Can you also share the seniority level required?", isSent = false),
        ChatMessage(
            "Sure — it's a Senior TPM role. 7+ years of experience required, " +
                    "ideally with a background in software delivery and stakeholder management.",
            isSent = true
        ),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(messages) { msg ->
            if (msg.isSent) {
                SentBubble(text = msg.text)
            } else {
                ReceivedBubble(text = msg.text)
            }
        }
    }
}