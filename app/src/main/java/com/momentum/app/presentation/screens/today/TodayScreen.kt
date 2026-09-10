package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Phase 0 Today placeholder: proves launch + theme + navigation work
 * and establishes the visual direction (gradient hero, layered surface).
 * Real Today experience lands in Phase 4.
 */
@Composable
fun TodayScreen() {
    val c = MomentumThemeColors.current
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Good morning",
            style = MaterialTheme.typography.bodyLarge,
            color = c.textSecondary,
        )
        Text(
            text = "Today",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 44.sp),
            color = c.textPrimary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(c.primary, c.secondary, c.accent),
                    ),
                )
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Your momentum starts here",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    "Phase 0 shell is live. Activities, streaks and insights arrive in upcoming phases.",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(c.surface.copy(alpha = 0.72f))
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("0%", style = MaterialTheme.typography.displayLarge, color = c.textPrimary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "No activities yet — check back in Phase 4.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textSecondary,
                )
            }
        }
    }
}
