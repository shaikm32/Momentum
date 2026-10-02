package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.LiquidGlassIconButton
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.theme.MomentumLabelMicro
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Home's **persistent top chrome** — Date capsule + Greeting + supporting line +
 * Settings, fixed (ADR-47 Amendment Note 5, human ruling §4.7 r1/§4.2 B;
 * ADR-48 Amendment Note 5 for the date capsule's glass pill).
 *
 * The chrome is a **sibling of the scroll region, above it** — never a scroll
 * item and never a `Surface`. It floats **inside** the atmosphere, over the
 * artwork's window, and reports its own measured height once so the scroll region
 * can start exactly at its bottom edge. That makes overlap **unrepresentable
 * rather than merely prevented**, with no collapse, no `NestedScrollConnection`,
 * no scroll observation and no parallax anywhere.
 *
 * **It is one coherent object, not three floating items** — by four guarantees
 * that are all checkable, and none of them is a plate:
 *
 * 1. **No plate.** No `Surface`, `Card`, `LiquidGlassCard`, `.background(`,
 *    divider or shadow sits between its own top and bottom edges. The copy sits
 *    on the raw atmosphere and the single scrim, exactly as the scrollable
 *    progress copy does. The only sub-surfaces are the two the approved design
 *    itself carries: the date **capsule** (a `GlassLevel.CONTROL` pill — the
 *    reference's `rounded-9999px rgba(255,255,255,0.4)` capsule, which amends
 *    ADR-47 Amendment Note 5 §3(a)'s "the *one* permitted sub-surface") and the
 *    48 dp `LiquidGlassIconButton`.
 * 2. **No divider and no edge.** Separation from the content below is by
 *    negative space alone ([ChromeBottomInset]).
 * 3. **One continuous scrim.** The atmosphere's single four-stop scrim runs
 *    behind the chrome and the list without a stop at `chromeBottom`.
 * 4. **One axis, one ink, one type scale, one material.** The same
 *    [MomentumSpacing.screen] left axis and `textPrimary` as every other piece of
 *    Home copy — ISSUE-020's remedy is the text token, never a re-inflated veil —
 *    and the same `GlassLevel.CONTROL` glass as every other control in the app.
 *
 * **It is grouped visually, never semantically.** The elements stay separate
 * semantics nodes — a merged chrome would swallow the Settings button and make a
 * persistent control unreachable by screen reader (§8.13, §8.17.6 rule 1) — and
 * the date capsule pill carries **no semantics of its own**.
 *
 * **The chrome grows with font scale** (it is measured, not fixed), and the
 * scroll region takes the remainder and still scrolls. At large font the chrome
 * may extend past the artwork window: that is accepted and is **not** a defect,
 * because the scrim spans the whole content area precisely so copy below the
 * window stays readable.
 *
 * @param revealProgress the screen's entrance phase for this region (offset 0 of
 *   the reference's 0 / 100 / 200 / 300 ms stagger). It is read **inside** the
 *   layer transform, so the chrome owns no animation instance.
 * @param onHeightChanged this chrome's measured height in px, reported upward so
 *   the scroll region can begin at a real edge. A pure function of the chrome's
 *   content, window insets and width — it never changes on scroll, so the write
 *   is idempotent and cannot feed back into layout.
 */
@Composable
fun HomeTopChrome(
    dateLabel: String,
    greeting: String,
    greetingSupport: String,
    onOpenSettings: () -> Unit,
    onHeightChanged: (Int) -> Unit,
    revealProgress: Float,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MomentumSpacing.screen)
            .testTag(HomeChromeTag)
            .onSizeChanged { size -> onHeightChanged(size.height) }
            .homeReveal(revealProgress),
    ) {
        // The artwork owns the window's top edge (ADR-33/37/39), so the chrome
        // insets the copy off the status bar while running on the artwork.
        Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Spacer(Modifier.height(MomentumSpacing.section))
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Top, not CenterVertically: the chrome is fixed and grows with font
            // scale, so a vertically-centred 48 dp control would drift against
            // the text column as the greeting wraps or grows.
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.standard),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
            ) {
                // The reference's date capsule: a glass pill. It carries no
                // semantics of its own — a date is a date, so heading navigation
                // still yields greeting -> Pending -> Completed and nothing else.
                Text(
                    text = dateLabel,
                    style = MomentumLabelMicro,
                    color = c.textPrimary,
                    modifier = Modifier.liquidGlass(
                        shape = RoundedCornerShape(MomentumRadii.pill),
                        level = GlassLevel.CONTROL,
                    )
                        .padding(
                            horizontal = MomentumSpacing.compact,
                            vertical = MomentumSpacing.micro,
                        ),
                )
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = c.textPrimary,
                    // The screen's first heading: heading navigation then yields
                    // greeting -> Pending -> Completed, which is the structural
                    // map a fixed chrome owes the user.
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = greetingSupport,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    // ISSUE-020: every Home copy token over the atmosphere is
                    // textPrimary — no textSecondary over the artwork, either
                    // theme. The reference's `#475569` is *not* imported.
                    color = c.textPrimary,
                )
            }
            LiquidGlassIconButton(
                icon = MomentumIcons.settings,
                contentDescription = stringResource(R.string.cd_settings),
                onClick = onOpenSettings,
            )
        }
        Spacer(Modifier.height(ChromeBottomInset))
    }
}

/**
 * The chrome's trailing breathing room, and the minimum separation between the
 * chrome and the first scrollable item. The design system's "gaps between major
 * sections" token — a named alias, not a literal, and the only spacing value
 * this composition introduces.
 */
internal val ChromeBottomInset = MomentumSpacing.section

/**
 * Layout-contract tag for the persistent chrome. Carries no accessibility
 * meaning; it exists so the composition (the chrome is fixed, sits over the
 * artwork, and the scroll region starts at its bottom edge) can be asserted at
 * bounds level.
 */
internal const val HomeChromeTag = "homeChrome"

@Preview(name = "Home chrome light", showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun HomeTopChromeLightPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeTopChrome(
            dateLabel = "FRI, SEP 25",
            greeting = "Good morning",
            greetingSupport = "Ready to conquer your day?",
            onOpenSettings = {},
            onHeightChanged = {},
            revealProgress = 1f,
        )
    }
}

@Preview(name = "Home chrome dark", showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun HomeTopChromeDarkPreview() {
    MomentumTheme(darkMode = DarkModeSetting.DARK) {
        HomeTopChrome(
            dateLabel = "FRI, SEP 25",
            greeting = "Good evening",
            greetingSupport = "Ready to conquer your day?",
            onOpenSettings = {},
            onHeightChanged = {},
            revealProgress = 1f,
        )
    }
}
