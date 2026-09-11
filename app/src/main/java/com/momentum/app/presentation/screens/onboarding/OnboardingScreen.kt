package com.momentum.app.presentation.screens.onboarding

import android.app.Activity as SystemActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumDurations
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
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

private data class OnboardingPageData(
    val scene: OnboardingScene,
    val headline: String,
    val body: String,
)

private val Pages = listOf(
    OnboardingPageData(
        scene = OnboardingScene.WELCOME,
        headline = "A better you starts with a small step.",
        body = "Build healthy habits, track progress, and create the life you want.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.FEATURES,
        headline = "Track what matters.",
        body = "Habits, routines, tasks and medicines — all in one calm and powerful app.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.PROGRESS,
        headline = "Stay consistent.",
        body = "Visual progress, streaks and insights help you keep going, day after day.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.BENEFITS,
        headline = "Small changes. Big results.",
        body = "A more focused, healthier and happier you is built through consistent, small steps.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.FINAL,
        headline = "Ready to build your momentum?",
        body = "Your journey to a brighter tomorrow starts now.",
    ),
)

/**
 * Immersive full-bleed onboarding. Functional contract is unchanged from the
 * previous implementation: page state advances with Continue, Skip finishes
 * without examples, and the final CTA completes onboarding — all through
 * [OnboardingViewModel.finish], which persists `onboardingDone`.
 */
@Composable
fun OnboardingRoute(onDone: () -> Unit) {
    val vm: OnboardingViewModel = momentumViewModel { OnboardingViewModel(it) }
    var page by remember { mutableIntStateOf(0) }
    val reduceMotion = rememberReduceMotion(null)

    // Photographic backgrounds need light status-bar icons regardless of the
    // app theme; restore the previous configuration on dispose. (The gesture
    // navigation bar keeps the system's default contrast enforcement.)
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? SystemActivity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val prevLightBars = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = false
        onDispose {
            if (prevLightBars != null) controller?.isAppearanceLightStatusBars = prevLightBars
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Cinematic backdrop with a whisper of parallax per page.
        val parallaxTarget = if (reduceMotion) 1f else 1f + page * 0.01f
        val parallax by animateFloatAsState(
            targetValue = parallaxTarget,
            animationSpec = tween(if (reduceMotion) 0 else 600),
            label = "onboardingParallax",
        )
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                if (reduceMotion) {
                    fadeIn(animationSpec = tween(MomentumDurations.fast)) togetherWith
                        fadeOut(animationSpec = tween(MomentumDurations.fast))
                } else {
                    (fadeIn(animationSpec = tween(350)) +
                        slideInHorizontally(animationSpec = tween(350)) { it / 4 }) togetherWith
                        (fadeOut(animationSpec = tween(250)) +
                            slideOutHorizontally(animationSpec = tween(250)) { -it / 4 })
                }
            },
            label = "onboardingScene",
        ) { current ->
            OnboardingBackdrop(scene = Pages[current].scene, parallax = parallax) {}
        }

        // Foreground frame: Skip, animated visual + copy, indicator, CTA.
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = MomentumSpacing.screen,
                        vertical = MomentumSpacing.group,
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(MomentumRadii.pill))
                        .clickable(role = Role.Button, onClick = { vm.finish(false, onDone) })
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.88f),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // Illustration zone.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        if (reduceMotion) {
                            fadeIn(animationSpec = tween(MomentumDurations.fast)) togetherWith
                                fadeOut(animationSpec = tween(MomentumDurations.fast))
                        } else {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(200))
                        }
                    },
                    label = "onboardingVisual",
                ) { current ->
                    PageVisual(page = current)
                }
            }

            // Copy zone.
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    if (reduceMotion) {
                        fadeIn(animationSpec = tween(MomentumDurations.fast)) togetherWith
                            fadeOut(animationSpec = tween(MomentumDurations.fast))
                    } else {
                        (fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(animationSpec = tween(300)) { it / 6 }) togetherWith
                            fadeOut(animationSpec = tween(200))
                    }
                },
                label = "onboardingCopy",
            ) { current ->
                Column(
                    modifier = Modifier.padding(horizontal = MomentumSpacing.screen),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                ) {
                    Text(
                        text = Pages[current].headline,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                    )
                    Text(
                        text = Pages[current].body,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                        ),
                        color = Color.White.copy(alpha = 0.88f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(MomentumSpacing.card))
            OnboardingPageIndicator(
                count = Pages.size,
                current = page,
                reduceMotion = reduceMotion,
                modifier = Modifier.padding(horizontal = MomentumSpacing.screen),
            )
            Spacer(modifier = Modifier.height(MomentumSpacing.card))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(
                        start = MomentumSpacing.screen,
                        end = MomentumSpacing.screen,
                        bottom = MomentumSpacing.card,
                    ),
            ) {
                if (page < Pages.lastIndex) {
                    OnboardingGlassButton(
                        text = "Continue",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { page++ },
                    )
                } else {
                    // Single final CTA. Internally this is the clean "fresh
                    // start" completion path — no demo activities are ever
                    // injected into the user's real data.
                    OnboardingGlassButton(
                        text = "Let's Get Started",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { vm.finish(false, onDone) },
                    )
                }
            }
        }
    }
}

/** Per-page floating illustration. Purely decorative (semantics cleared). */
@Composable
private fun PageVisual(page: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        when (page) {
            1 -> FeatureTiles()
            2 -> MiniTodayPanel()
            3 -> BenefitsComposition()
            else -> Unit
        }
    }
}

@Composable
private fun FeatureTiles() {
    Column(
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card)) {
            OnboardingGlassIconTile(
                icon = momentumIcon("fitness"),
                contentDescription = null,
                label = "Activity",
            )
            OnboardingGlassIconTile(
                icon = momentumIcon("learning"),
                contentDescription = null,
                label = "Learning",
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card)) {
            OnboardingGlassIconTile(
                icon = momentumIcon("music"),
                contentDescription = null,
                label = "Wellness",
            )
            OnboardingGlassIconTile(
                icon = momentumIcon("water"),
                contentDescription = null,
                label = "Health",
            )
        }
    }
}

/** Static miniature of the Today experience — illustration only, no live data. */
@Composable
private fun MiniTodayPanel() {
    OnboardingGlassPanel(
        modifier = Modifier.padding(horizontal = MomentumSpacing.screen * 2),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                    text = "3 of 5 complete",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f),
                )
            }
            ProgressOrb(progress = 0.6f, modifier = Modifier, size = 64.dp, glowEnabled = false)
        }
        MiniActivityRow(iconKey = "water", name = "Drink water", done = true)
        MiniActivityRow(iconKey = "fitness", name = "Morning workout", done = false)
        MiniActivityRow(iconKey = "learning", name = "Read", done = false)
    }
}

@Composable
private fun MiniActivityRow(iconKey: String, name: String, done: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        OnboardingGlassIconTile(
            icon = momentumIcon(iconKey),
            contentDescription = null,
            tileSize = 40.dp,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.weight(1f),
        )
        if (done) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF062A26),
                    modifier = Modifier.size(14.dp),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(1.5f.dp, Color.White.copy(alpha = 0.45f)),
                        CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun BenefitsComposition() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(56.dp),
        ) {
            OnboardingGlassIconTile(
                icon = Icons.Filled.SelfImprovement,
                contentDescription = null,
                tileSize = 56.dp,
                label = "Healthier\nHabits",
            )
            OnboardingGlassIconTile(
                icon = Icons.Filled.LightMode,
                contentDescription = null,
                tileSize = 56.dp,
                label = "Greater\nFocus",
            )
        }
        OnboardingGlassOrb(orbSize = 168.dp) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = Color(0xFF86EFAC),
                modifier = Modifier.size(64.dp),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(56.dp),
        ) {
            OnboardingGlassIconTile(
                icon = Icons.Filled.Favorite,
                contentDescription = null,
                tileSize = 56.dp,
                label = "Less\nStress",
            )
            OnboardingGlassIconTile(
                icon = Icons.Filled.BarChart,
                contentDescription = null,
                tileSize = 56.dp,
                label = "A Brighter\nYou",
            )
        }
    }
}
