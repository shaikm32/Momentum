package com.momentum.app.presentation.screens.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Home's single progress **state** (ADR-48 / refinement R1), derived from
 * [TodayUiState.Success] by the screen and passed as state so the card stays
 * previewable and owns no app state. Three branches, unchanged from TASK-017.
 */
sealed interface HeroFigureState {
    /** `0 < done < total`: the fraction row, the ring carrying the percentage. */
    data class Fraction(val done: Int, val total: Int) : HeroFigureState

    /** `done == total > 0`: the statement replaces the fraction row, ring full. */
    data class Complete(val done: Int, val total: Int) : HeroFigureState

    /** `total == 0`: no progress block, no ring, no reserved space. */
    data object Hidden : HeroFigureState
}

/**
 * Progress-card ring diameter (ADR-48 Amendment Note 5): the reference's
 * `w-24 h-24`, which is also the largest opaque element Home's atmosphere
 * carries — so it, and not the retired 80 dp [HeroRingSize], is the basis of
 * BOUND-1 in `HomeAtmosphere.kt`.
 *
 * Its stroke is unchanged at `size × 0.075` (7.2 dp, the reference's `7`), and
 * its radius is `MomentumRadii.control` (12 dp) in the reference's own terms.
 */
internal val ProgressCardRingSize = 96.dp

/**
 * The encouragement line (ADR-48 Amendment Note 1) — retained **verbatim** in
 * behaviour by ADR-48 Amendment Note 5 and re-hosted here because it is the
 * day's progress copy rather than a card surface.
 *
 * An optional state-specific line under the card's metric row; when null the
 * child and its spacing are absent — no reserved space. Its exit keeps painting
 * the last line while it shrinks away, and **reduced motion skips the whole
 * visibility transition** (`EnterTransition.None` / `ExitTransition.None`) rather
 * than shortening it. The Settings toggle therefore still removes the line
 * completely, and the line keeps its **own** announcement — it sits outside the
 * card's merged description.
 *
 * @param color the line's ink, supplied by `HomeCards.kt`: `textPrimary` for
 *   both the encouragement and the "Today is complete" statement. The approved
 *   emerald tint was re-inked because it measures 3.72:1 on the Light progress
 *   card against the ≥ 4.5:1 body-text floor, and `textSecondary` was rejected
 *   in turn because the card's chromatic wash makes the *darkest* composited
 *   stop the governing case (3.03–3.91:1 across the plausible backdrops) —
 *   ADR-49 Amendment Note 3, `task.md` §10.7. The default below is the retained
 *   `textPrimary` (ADR-48 Amendment Note 5) and no production caller relies on
 *   it.
 */
@Composable
internal fun HomeEncouragementLine(
    encouragement: String?,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MomentumThemeColors.current.textPrimary,
) {
    // The exit transition must keep painting the last line while it shrinks
    // away; the holder only changes when a new line arrives, so it converges.
    var lastLine by remember { mutableStateOf(encouragement) }
    if (encouragement != null) lastLine = encouragement

    AnimatedVisibility(
        visible = encouragement != null,
        enter = if (reduceMotion) {
            EnterTransition.None
        } else {
            fadeIn(tween(durationMillis = MomentumMotion.standard, easing = MomentumMotion.easeOut)) +
                expandVertically(tween(MomentumMotion.standard))
        },
        exit = if (reduceMotion) {
            ExitTransition.None
        } else {
            fadeOut(tween(MomentumMotion.fast)) + shrinkVertically(tween(MomentumMotion.fast))
        },
        modifier = modifier,
    ) {
        Column {
            Spacer(Modifier.height(MomentumSpacing.micro))
            Text(
                text = lastLine.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "Encouragement on", showBackground = true, widthDp = 360, heightDp = 120)
@Composable
private fun HomeEncouragementOnPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        HomeEncouragementLine(encouragement = "Next up: Morning run", reduceMotion = false)
    }
}

@Preview(name = "Encouragement off", showBackground = true, widthDp = 360, heightDp = 120)
@Composable
private fun HomeEncouragementOffPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeEncouragementLine(encouragement = null, reduceMotion = false)
    }
}
