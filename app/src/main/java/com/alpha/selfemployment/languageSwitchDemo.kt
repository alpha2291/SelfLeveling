package com.alpha.selfemployment

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun LocalizationPreviewScreen() {

    val languages = listOf("English", "हिंदी", "தமிழ்", "తెలుగు", "മലയാളം", "ಕನ್ನಡ")
    var selectedLanguage by remember { mutableStateOf("English") }
    var expanded by remember { mutableStateOf(false) }

    // Map language → locale code
    val localeMap = mapOf(
        "English" to "en",
        "हिंदी" to "hi",
        "தமிழ்" to "ta",
        "తెలుగు" to "te",
        "മലയാളം" to "ml",
        "ಕನ್ನಡ" to "kn"
    )

    // Apply locale to context
    val context = LocalContext.current
    val localizedContext = remember(selectedLanguage) {
        val locale = java.util.Locale(localeMap[selectedLanguage] ?: "en")
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    // Helper to get string from localizedContext
    @Composable
    fun str(id: Int): String = localizedContext.getString(id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // ── TOP BAR ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = str(R.string.app_name),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // ── LANGUAGE DROPDOWN ─────────────────────────────
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedLanguage, color = Color.White, fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("▾", color = Color.White, fontSize = 14.sp)
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                selectedLanguage = lang
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── LOGIN CARD ────────────────────────────────────
            item {
                SectionCard {
                    SectionTitle("Login Screen")
                    PreviewField(str(R.string.mobile_number))
                    PreviewField(str(R.string.enter_mobile_number))
                    PreviewField(str(R.string.email_id))
                    PreviewField(str(R.string.enter_email_id))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewButton(str(R.string.login), Color(0xFF1565C0), Modifier.weight(1f))
                        PreviewButton(str(R.string.register), Color(0xFF43A047), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(str(R.string.login_description), fontSize = 12.sp, color = Color.Gray)
                    Text(str(R.string.login_to_register_switch), fontSize = 12.sp, color = Color(0xFF1565C0))
                }
            }

            // ── OTP CARD ──────────────────────────────────────
            item {
                SectionCard {
                    SectionTitle("OTP / Verification")
                    Text(str(R.string.verification), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(str(R.string.otp_notReceived) + " ", fontSize = 12.sp, color = Color.Gray)
                    Text(str(R.string.otp_error), fontSize = 12.sp, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewButton(str(R.string.verify), Color(0xFF1565C0), Modifier.weight(1f))
                        PreviewButton(str(R.string.resend), Color(0xFF757575), Modifier.weight(1f))
                    }
                }
            }

            // ── PROFILE CARD ──────────────────────────────────
            item {
                SectionCard {
                    SectionTitle("Profile")
                    PreviewField(str(R.string.username))
                    PreviewField(str(R.string.add_bio))
                    PreviewField(str(R.string.type_your_bio))
                    Spacer(Modifier.height(4.dp))
                    Text(str(R.string.username_change_error), fontSize = 11.sp, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewButton(str(R.string.save_changes), Color(0xFF1565C0), Modifier.weight(1f))
                        PreviewButton(str(R.string.cancel), Color(0xFF757575), Modifier.weight(1f))
                    }
                }
            }

            // ── FOLLOW CARD ───────────────────────────────────
            item {
                SectionCard {
                    SectionTitle("Follow / Social")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewChip(str(R.string.followers))
                        PreviewChip(str(R.string.following))
                        PreviewChip(str(R.string.likes))
                        PreviewChip(str(R.string.comments))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewButton(str(R.string.follow_back), Color(0xFF1565C0), Modifier.weight(1f))
                        PreviewButton(str(R.string.unfollow), Color(0xFFE53935), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(str(R.string.unfollow_alert_content), fontSize = 11.sp, color = Color.Gray)
                }
            }

            // ── SETTINGS CARD ─────────────────────────────────
            item {
                SectionCard {
                    SectionTitle("Settings")
                    listOf(
                        R.string.account_settings,
                        R.string.my_interests,
                        R.string.saved_business,
                        R.string.notification,
                        R.string.allow_notification,
                        R.string.my_blocklist,
                        R.string.contact,
                    ).forEach { resId ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(str(resId), fontSize = 13.sp)
                            Text("›", color = Color.Gray)
                        }
                        Divider(color = Color(0xFFEEEEEE))
                    }
                }
            }

            // ── DANGER ZONE CARD ──────────────────────────────
            item {
                SectionCard {
                    SectionTitle("Danger Zone")
                    Text(str(R.string.account_deletion_title_description), fontWeight = FontWeight.SemiBold, color = Color.Red)
                    Spacer(Modifier.height(4.dp))
                    Text(str(R.string.account_deletion_option_description), fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(str(R.string.logout_alert_content), fontSize = 11.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PreviewButton(str(R.string.logout), Color(0xFFFF6F00), Modifier.weight(1f))
                        PreviewButton(str(R.string.delete_account), Color(0xFFE53935), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ── HELPER COMPOSABLES ────────────────────────────────────────

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1565C0),
        letterSpacing = 1.sp
    )
    Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE3F2FD))
}

@Composable
fun PreviewField(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 13.sp, color = Color(0xFF757575))
    }
}

@Composable
fun PreviewButton(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PreviewChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE3F2FD))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF1565C0))
    }
}