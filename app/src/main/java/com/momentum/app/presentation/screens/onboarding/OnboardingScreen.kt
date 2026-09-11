package com.momentum.app.presentation.screens.onboarding

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumDurations
import com.momentum.app.presentation.theme.MomentumHeights
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.LocalDate
import kotlinx.coroutines.launch

class OnboardingViewModel(private val container: AppContainer) : ViewModel() {

    fun finish(seedExamples: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            if (seedExamples) seed()
            container.settingsRepository.update { it.copy(onboardingDone = true) }
            onDone()
        }
    }

    private suspend fun seed() {
        val today = LocalDate.now()
        val examples = listOf(
            Activity(title = "Drink water", iconKey = "water", startDate = today, recurrence = Recurrence.Daily, targetPerOccurrence = 8.0, unit = "glasses", priority = 2),
            Activity(title = "Read 20 pages", iconKey = "learning", startDate = today, recurrence = Recurrence.Daily, targetPerOccurrence = 20.0, unit = "pages", priority = 1),
            Activity(title = "Evening walk", iconKey = "fitness", startDate = today, recurrence = Recurrence.Daily, priority = 1),
        )
        runCatching { examples.forEach { container.activityRepository.upsert(it) } }
    }
}

private data class OnboardingPage(
    val iconKey: String,
    val title: String,
    val body: String,
)

private val Pages = listOf(
    OnboardingPage(
        iconKey = "spark",
        title = "Small steps, unstoppable momentum.",
        body = "Track habits, routines, tasks, and medicines in one calm place.",
    ),
    OnboardingPage(
        iconKey = "timer",
        title = "Your progress, visualized.",
        body = "Streaks, trends, and a calendar heatmap show consistency building.",
    ),
    OnboardingPage(
        iconKey = "home",
        title = "Private by design.",
        body = "Everything stays on your device. No account, no cloud, works offline.",
    ),
)

/** Short, skippable onboarding → straight into creating the first activity. */
@Composable
fun OnboardingRoute(onDone: () -> Unit) {
    val vm: OnboardingViewModel = momentumViewModel { OnboardingViewModel(it) }
    var page by remember { mutableIntStateOf(0) }
    val c = MomentumThemeColors.current
    val reduceMotion = rememberReduceMotion(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.section,
            ),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
    ) {
        Spacer(Modifier.weight(0.3f))
        // Floating hero orb: glass disc with a soft primary glow. The ambient
        // background shines through — first impression, not a marketing card.
        Box(
            modifier = Modifier
                .size(112.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = c.primary.copy(alpha = 0.4f),
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        0f to Color.White.copy(alpha = 0.16f),
                        0.55f to c.surface.copy(alpha = 0.5f),
                        1f to c.surface.copy(alpha = 0.72f),
                    ),
                    shape = CircleShape,
                )
                .border(
                    BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.25f)),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = momentumIcon(Pages[page].iconKey),
                contentDescription = null,
                tint = c.primary,
                modifier = Modifier.size(44.dp),
            )
        }
        AnimatedPageContent(visiblePage = page, reduceMotion = reduceMotion) {
            Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                Text(
                    Pages[page].title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = c.textPrimary,
                )
                Text(
                    Pages[page].body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = c.textSecondary,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Pages.indices.forEach { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(MomentumRadii.pill))
                        .background(if (i <= page) c.primary else c.surfaceVariant),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        if (page < Pages.lastIndex) {
            MomentumButton(text = "Continue", onClick = { page++ })
            OnboardingSkip(label = "Skip", onClick = { vm.finish(false, onDone) })
        } else {
            MomentumButton(text = "Start with examples", onClick = { vm.finish(true, onDone) })
            OnboardingSkip(label = "Start fresh", onClick = { vm.finish(false, onDone) })
        }
    }
}

@Composable
private fun AnimatedPageContent(
    visiblePage: Int,
    reduceMotion: Boolean,
    content: @Composable () -> Unit,
) {
    if (reduceMotion) {
        content()
    } else {
        androidx.compose.animation.AnimatedContent(
            targetState = visiblePage,
            transitionSpec = {
                (fadeIn(animationSpec = tween(MomentumDurations.medium)) +
                    expandVertically(animationSpec = tween(MomentumDurations.medium))) togetherWith
                    fadeOut(animationSpec = tween(MomentumDurations.fast))
            },
            label = "onboardingPage",
        ) {
            content()
        }
    }
}

@Composable
private fun OnboardingSkip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = MomentumHeights.controlMin)
            .clip(RoundedCornerShape(MomentumRadii.control))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MomentumThemeColors.current.textSecondary,
            fontWeight = FontWeight.Medium,
        )
    }
}
