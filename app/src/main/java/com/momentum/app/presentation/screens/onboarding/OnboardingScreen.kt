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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.AppContainer
import com.momentum.app.domain.model.Activity
import com.momentum.app.domain.model.Recurrence
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumDurations
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.Twilight
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
        scene = OnboardingScene.INTENT,
        headline = "A more intentional you.",
        body = "Build healthy habits, track progress and create the life you want.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.TRACK,
        headline = "Track what matters.",
        body = "Habits, routines, tasks, medicines and more, all in one calm and powerful app.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.GROWTH,
        headline = "Stay Consistent",
        body = "Visual progress and insights help you keep going day after day.",
    ),
    OnboardingPageData(
        scene = OnboardingScene.BEGIN,
        headline = "Ready to build Momentum?",
        body = "Your journey to a brighter tomorrow starts now.",
    ),
)

/**
 * Immersive full-bleed onboarding. Functional contract is unchanged from the
 * previous implementation: page state advances with Continue, Skip finishes
 * without examples, and the final CTA completes onboarding — all through
 * [OnboardingViewModel.finish], which persists `onboardingDone`.
 *
 * Each of the four artworks already contains its scene's illustrations, so
 * no Compose overlay is drawn over them — the foreground is only Skip,
 * headline/body, the 4-state indicator, and the CTA.
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
        // Cinematic backdrop with a whisper of parallax per page. Capped at
        // 2% extra magnification so the artwork is never visibly over-zoomed;
        // edge-safe (uniform scale, never translation, so no bars can appear).
        val parallaxTarget = if (reduceMotion) 1f else 1f + page * 0.005f
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
                OnboardingGlassSkipButton(onClick = { vm.finish(false, onDone) })
            }

            // Breathing room: the artwork carries each scene's visuals, so
            // this flex zone simply lets it show through above the copy.
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            // Copy zone.
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    if (reduceMotion) {
                        fadeIn(animationSpec = tween(MomentumDurations.fast)) togetherWith
                            fadeOut(animationSpec = tween(200))
                    } else {
                        (fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(animationSpec = tween(300)) { it / 6 }) togetherWith
                            fadeOut(animationSpec = tween(200))
                    }
                },
                label = "onboardingCopy",
            ) { current ->
                // Screen 1 only: a whisper of soft shadow lifts the copy off
                // the bright lower lake without touching the artwork or the
                // shared gradient — the smallest readability treatment that
                // works. Other pages keep their existing treatment.
                val copyShadow = if (current == 0) {
                    Shadow(color = Color.Black.copy(alpha = 0.35f), blurRadius = 12f)
                } else {
                    null
                }
                Column(
                    modifier = Modifier.padding(horizontal = MomentumSpacing.screen),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                ) {
                    Text(
                        text = Pages[current].headline,
                        style = MaterialTheme.typography.displayLarge.copy(shadow = copyShadow),
                        color = Twilight.Moonlight,
                    )
                    Text(
                        text = Pages[current].body,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            shadow = copyShadow,
                        ),
                        color = Twilight.Moonlight.copy(alpha = 0.88f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(MomentumSpacing.card))
            // Indicator row centered on the CTA axis (shared chrome: the same
            // centered composition reads correctly on every page).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                OnboardingPageIndicator(
                    count = Pages.size,
                    current = page,
                    reduceMotion = reduceMotion,
                )
            }
            Spacer(modifier = Modifier.height(MomentumSpacing.card))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = MomentumSpacing.card,
                    ),
            ) {
                if (page < Pages.lastIndex) {
                    OnboardingGlassButton(
                        text = "Continue",
                        onClick = { page++ },
                    )
                } else {
                    // Single final CTA. Internally this is the clean "fresh
                    // start" completion path — no demo activities are ever
                    // injected into the user's real data.
                    OnboardingGlassButton(
                        text = "Let's Get Started",
                        onClick = { vm.finish(false, onDone) },
                    )
                }
            }
            // Screen 1 only: a breath of extra room below the CTA so the
            // headline/body sit slightly higher with clear air above the
            // system area. Bounded and fixed — the flex illustration zone
            // above keeps the composition responsive on tall screens.
            if (page == 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
