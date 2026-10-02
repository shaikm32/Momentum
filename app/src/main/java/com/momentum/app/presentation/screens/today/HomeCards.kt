package com.momentum.app.presentation.screens.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.momentum.app.R
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.usecase.TodayItem
import com.momentum.app.presentation.components.CompletionAccent
import com.momentum.app.presentation.components.CompletionButton
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumIcons
import com.momentum.app.presentation.components.ProgressOrb
import com.momentum.app.presentation.components.completionActionLabel
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.theme.MomentumHeadlineCompact
import com.momentum.app.presentation.theme.MomentumHeadlineSection
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumLabelMicro
import com.momentum.app.presentation.theme.MomentumMotion
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors
import com.momentum.app.presentation.theme.habitIconAccent
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * Home's three **screen-local** card surfaces, translated from the approved
 * Stitch Aurora reference (ADR-48 Amendment Note 5, human ruling 2026-10-01
 * §4.7 r1–r4).
 *
 * They are screen-local by necessity, not by preference:
 *
 * - **No streak architecture exists.** The reference's `7d` / `12d` chips are
 *   removed by the human ruling (§4.7 r3 / §7), so a card carries exactly what
 *   the domain already provides — icon, title, the existing metadata line, and
 *   the completion control — and nothing here invents a duration or an entry
 *   count (§4.7 r4, §8.12 C3).
 * - **`ActivityCard` is untouched.** Its 44 dp flat tint tile, `titleLarge`, no
 *   strikethrough, faint idle glyph and `primary → secondary` done fill are what
 *   Tasks, Calendar and Habit Detail already ship. `HomeActivityCard` composes the
 *   **same shared primitives** — `Modifier.liquidGlass`, `glassPress`,
 *   `momentumIcon`, `habitIconAccent`, `CompletionButton` — so it does not
 *   reimplement glass and does not fork a control.
 * - **`MomentumSectionHeader` is untouched.** It has 29 call sites across 11
 *   files at `titleMedium` (16/24 400); changing it would redesign Tasks,
 *   Calendar, Insights, Settings, the detail and the creation screens.
 *
 * **Reversal is preserved everywhere.** The reference's completed badge is
 * *non-interactive*; Momentum's stays a real control with its role and its action
 * label, because a tap must still un-complete the activity (ADR-47 §21,
 * `DECISIONS` §12).
 */
@Preview(name = "Home progress card light", showBackground = true, widthDp = 360, heightDp = 200)
@Composable
private fun HomeProgressCardPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        Box(Modifier.padding(MomentumSpacing.screen)) {
            HomeProgressCard(
                figureState = HeroFigureState.Fraction(done = 3, total = 5),
                encouragement = "Great momentum today! Keep it flowing.",
                completedCount = 3,
                pendingCount = 2,
                milestoneGlow = false,
                reduceMotion = true,
                ambientPulse = null,
                revealProgress = 1f,
            )
        }
    }
}

@Preview(name = "Home section header light", showBackground = true, widthDp = 360, heightDp = 60)
@Composable
private fun HomeSectionHeaderPreview() {
    MomentumTheme(darkMode = DarkModeSetting.LIGHT) {
        HomeSectionHeader(
            title = "Pending",
            meta = "Scheduled Today",
            modifier = Modifier.padding(MomentumSpacing.screen),
        )
    }
}

/**
 * The Daily Momentum card — the approved **horizontal** Aurora lockup
 * (ADR-48 Amendment Note 5, human ruling §4.2 C). The rejected TASK-017
 * vertical arrangement ("ring above the count") is deleted outright, not
 * patched.
 *
 * ```
 * Box
 *   Box(matchParentSize, clipToBounds)          <- the reference's overflow-hidden
 *     AuraBlobA(align TopEnd,    offset -48, -48)
 *     AuraBlobB(align BottomStart, offset -40, +40)
 *   Column(liquidGlass(RoundedCornerShape(MomentumRadii.control), HERO,
 *                      hue = auroraLavender).padding(MomentumSpacing.standard))
 *     Row(CenterVertically, spacedBy(standard))
 *       Column(weight 1f, clearAndSetSemantics { today_hero_progress_desc })
 *         Row(Bottom) Text("3 of 5", MomentumHeadlineCompact) + Text(suffix)
 *         HomeEncouragementLine(encouragement ?: statement)
 *       Box(ProgressCardRingSize, Center)
 *         ProgressOrb(arcBrush = success -> auroraBlue -> auroraViolet, showPercent = false)
 *         Column(Center) Text("60%", MomentumHeadlineSection) + Text("done", MomentumLabelMicro)
 *     HorizontalDivider(Hairline, textPrimary @ 0.08)
 *     Spacer(MomentumSpacing.compact)
 *     Row(SpaceBetween) success dot + "3 Completed" · muted dot + "2 Pending"
 * ```
 *
 * **Values are the reference's own**, mapped onto existing tokens: the 96 dp
 * ring (`w-24 h-24`), the 12 dp radius (`MomentumRadii.control`), 16 dp padding
 * (`MomentumSpacing.standard`), 8 dp dots, and a ring gradient of three
 * **existing** theme tokens — `success` `#00a572` → `auroraBlue` `#06b6d4` →
 * `auroraViolet` `#7c3aed` — so **no new colour** is introduced. The ring's track
 * is `textPrimary @ 0.7` (ISSUE-014's ≥ 3:1 over artwork outranks the
 * reference's `0.08`; the alpha was raised `0.55 → 0.7` for this Home card,
 * whose sole production `ProgressOrb` consumer it is — ADR-49 Amendment Note 5).
 *
 * **The encouragement / statement line reads `textPrimary`** (ADR-49
 * Amendment Note 3). It was §8.7's `c.success`, which measures **3.72:1** on
 * this card in Light against the **≥ 4.5:1** body-text floor; §10.1 then moved it
 * to `c.textSecondary`, but that ruling rested on a **method-inverted**
 * premise — "the worst case is the glass over pure white" holds for a *solid*
 * fill and is **backwards** for this card's chromatic wash, which paints
 * `lerp(surface, auroraLavender, 0.55)` at `heroFill = 0.68` **over** the
 * backdrop, so the governing case is the card's *darkest* composited stop.
 * `c.textSecondary` therefore measures **3.91:1** over white, **3.81:1** over the
 * Light page, **3.55:1** over `#E0E0F0` and **3.03:1** over a dark artwork
 * region — every one below the floor. `c.textPrimary` (`#172033`) clears it over
 * every stop and every backdrop by a wide margin, is the ink TASK-017 already
 * used for this copy, adds **no new colour token** and moves **no scrim peak**.
 * The **ring gradient is exempt and the two breakdown dots take `textPrimary`**
 * (ADR-49 Amendment Note 5, `task.md` §4.11 CHANGE 4/5). The `success →
 * auroraBlue → auroraViolet` arc measures 1.00:1 against the track but is
 * classified **decorative**, not the sole carrier of progress: the fraction, the
 * `%` number and the `n Completed / n Pending` row all state it textually, and
 * forcing the arc to 3:1 would need an ink arc that reverses the approved
 * gradient (§4.11 CHANGE 5's five conditions hold). The two dots are non-text
 * boundaries that must clear 3:1, so each is `c.textPrimary`; the relation
 * between the halves is carried by the labels, not by two dot colours.
 *
 * **The card's body text is uniformly `textPrimary` — do not "restore" the
 * reference's `#475569` hierarchy.** The reasoning above applies to *every*
 * band on this card, not only the encouragement, because the chromatic wash is
 * the card's whole background: `c.textSecondary` measures **3.91:1** at its
 * lightest composited stop and **3.03:1** at its darkest, so it fails the
 * **≥ 4.5:1** body-text floor *everywhere on this card*, not at one spot.
 * The human ruling of 2026-10-01 (`task.md` §13.1, option (a) adopted)
 * therefore re-inked the three remaining `textSecondary` bands to
 * `c.textPrimary`: the `items done` suffix, the ring's `done` unit, and
 * `n Pending`. The `n Completed` band was already `c.textPrimary`, so the
 * breakdown row is now uniform too. `c.textPrimary` adds **no new colour
 * token**, moves **no scrim peak**, changes **no alpha** and leaves the
 * `liquidGlass` wash, gradient and `HueMix` untouched.
 *
 * **This is an explicit, approved accessibility-driven deviation from the
 * reference**, and the uniform ink is deliberate rather than accidental
 * simplification: **a slate hierarchy is not available on this card.** Do not
 * re-introduce `c.textSecondary` here to recover it. Contrast on the artwork
 * geometry itself is still unmeasured (`adb` unavailable), and the bands above
 * the card — `Scheduled Today` in particular — are bare over the atmosphere,
 * not on this wash, and are ruled separately.
 *
 * **One merged description.** `today_hero_progress_desc` covers the whole merged
 * group — fraction, suffix, percentage, `done` and the breakdown — so the lockup
 * is never read twice, and the ring stays decorative (`showPercent = false`). The
 * encouragement sits **outside** that group and keeps its own announcement.
 *
 * **The denominator is visible.** The metric row reads `3 of 5` with `items
 * done` as its subordinate suffix (§4.2 C, `screen.png`, `code.html:92`,
 * `task.md` §10.8 Ruling B): both values already arrive in
 * [HeroFigureState.Fraction], so nothing new is plumbed and no new string token
 * is minted beyond `today_hero_fraction` itself.
 *
 * **The ring's position is decided by the approved composition**, never by
 * available width: the text column is `weight(1f)` and the ring is
 * `size`-constrained, so it is invariant at every width, digit count and font
 * scale (ADR-48 §1's invariant preserved by ADR-48 Amendment Note 5). `tnum` on
 * [MomentumHeadlineCompact] keeps the fraction from reflowing as `done` advances.
 *
 * **States.** `Fraction` renders as above. `Complete` fills the ring and replaces
 * the fraction row with `today_hero_complete` at the same token, and states the
 * line in the **same `textPrimary`** as the encouragement, because there is no
 * encouragement (`encouragementFor` already returns null there). `Hidden` is
 * **not composed** by the screen at all, so it reserves nothing and `emptyRegion`
 * centring is unaffected.
 *
 * **The two aura blobs** (ADR-48 Amendment Note 6, M2) are the reference's
 * `aurora-glow-pulse`: the screen's one 6 s clock drives two `drawWithCache`
 * radial gradients whose **cached** radius is scaled 32 → 40 dp per frame while
 * their alpha breathes 0.35 ↔ 0.60. `canvas.scale` scales the cached shader's
 * geometry and its falloff together, so the animated softness costs **one** brush
 * and **zero** per-frame allocation; `filter: blur()` itself remains a documented
 * non-adoption (ADR-49 §4) — the blobs were always painted soft, and the pulse
 * animates that softness. The clipped box crops the negative offsets exactly as
 * the reference's `overflow-hidden` does. The blobs carry no semantics, are not
 * interactive and add no text band.
 *
 * @param ambientPulse the screen's **shared** 6 s clock. It arrives as a
 *   `State<Float>` and is read only inside `drawWithCache`, so the card never
 *   recomposes for it and no animation instance exists inside a list item
 *   (ADR-48 Amendment Note 6 §4). `null` under reduced motion, where each blob
 *   rests on its own 0%/100% frame and nothing jumps.
 */
@Composable
internal fun HomeProgressCard(
    figureState: HeroFigureState,
    encouragement: String?,
    completedCount: Int,
    pendingCount: Int,
    milestoneGlow: Boolean,
    reduceMotion: Boolean,
    ambientPulse: State<Float>?,
    revealProgress: Float,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val isComplete = figureState is HeroFigureState.Complete
    val done = when (figureState) {
        is HeroFigureState.Fraction -> figureState.done
        is HeroFigureState.Complete -> figureState.done
        HeroFigureState.Hidden -> 0
    }
    val total = when (figureState) {
        is HeroFigureState.Fraction -> figureState.total
        is HeroFigureState.Complete -> figureState.total
        HeroFigureState.Hidden -> 0
    }
    val progress = if (isComplete) {
        1f
    } else if (total > 0) {
        done.toFloat() / total
    } else {
        0f
    }
    val description = stringResource(R.string.today_hero_progress_desc, done, total)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .homeReveal(revealProgress)
            .testTag(HomeProgressCardTag),
    ) {
        // The reference's `relative overflow-hidden` card: the blobs are beneath
        // the content row and cropped at the card's own edges, while the card's
        // own shadow is drawn by its `liquidGlass` modifier and is never clipped.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clipToBounds(),
        ) {
            AuraBlobA(
                color = c.primary,
                phase = ambientPulse,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = -AuraBlobAOverhang, y = -AuraBlobAOverhang),
            )
            AuraBlobB(
                color = c.secondary,
                phase = ambientPulse,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = -AuraBlobBOverhang, y = AuraBlobBOverhang),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(MomentumRadii.control),
                    level = GlassLevel.HERO,
                    // ADR-48 Amendment Note 5: the one documented glass
                    // extension, carrying the reference's own 135° chromatic
                    // wash in theme tokens. `null` everywhere else.
                    hue = c.auroraLavender,
                    // ADR-49 Amendment Note 5 (task.md §4.11): Home-scoped fill
                    // opt-in. Raises this card to the rejected E3 0.85 without a
                    // global token change; `null` is byte-identical elsewhere.
                    fillAlpha = ProgressCardFillAlpha,
                )
                .padding(MomentumSpacing.standard),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.standard),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clearAndSetSemantics { contentDescription = description },
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        if (isComplete) {
                            Text(
                                text = stringResource(R.string.today_hero_complete),
                                style = MomentumHeadlineCompact,
                                color = c.textPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.today_hero_fraction, done, total),
                                style = MomentumHeadlineCompact,
                                color = c.textPrimary,
                            )
                            Spacer(Modifier.size(MomentumSpacing.micro))
                            Text(
                                text = stringResource(R.string.today_progress_suffix),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                // Human ruling 2026-10-01, `task.md` §13.1: the card's
                                // body text is uniformly `textPrimary`, because
                                // `textSecondary` cannot clear 4.5:1 on this wash.
                                color = c.textPrimary,
                            )
                        }
                    }
                    HomeEncouragementLine(
                        encouragement = encouragement,
                        // ADR-49 Amendment Note 3 (task.md §10.7): `textPrimary`.
                        color = c.textPrimary,
                        reduceMotion = reduceMotion,
                    )
                }
                Box(
                    modifier = Modifier.size(ProgressCardRingSize),
                    contentAlignment = Alignment.Center,
                ) {
                    ProgressOrb(
                        progress = progress,
                        size = ProgressCardRingSize,
                        showPercent = false,
                        arcBrush = Brush.linearGradient(
                            colors = listOf(c.success, c.auroraBlue, c.auroraViolet),
                        ),
                        glowEnabled = milestoneGlow && !reduceMotion,
                        animate = !reduceMotion,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MomentumHeadlineSection,
                            color = c.textPrimary,
                        )
                        Text(
                            text = stringResource(R.string.today_progress_ring_unit),
                            style = MomentumLabelMicro,
                            // `task.md` §13.1: `textPrimary`, not the reference's
                            // `#475569` — see [HomeProgressCard]'s KDoc.
                            color = c.textPrimary,
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = Dp.Hairline,
                color = c.textPrimary.copy(alpha = DividerAlpha),
            )
            Spacer(Modifier.height(MomentumSpacing.compact))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
                ) {
                    Box(
                        Modifier
                            .size(BreakdownDotSize)
                            .background(c.textPrimary, CircleShape),
                    )
                    Text(
                        text = stringResource(R.string.cd_progress_completed, completedCount),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = c.textPrimary,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.tight),
                ) {
                    Box(
                        Modifier
                            .size(BreakdownDotSize)
                            .background(c.textPrimary, CircleShape),
                    )
                    Text(
                        text = stringResource(R.string.cd_progress_pending, pendingCount),
                        style = MaterialTheme.typography.labelMedium,
                        // `task.md` §13.1: `textPrimary`, matching the `n Completed`
                        // half of this row — see [HomeProgressCard]'s KDoc.
                        color = c.textPrimary,
                    )
                }
            }
        }
    }
}

/**
 * A section heading with its right-hand status label (ADR-48 Amendment Note 5):
 * the heading is a screen-reader **heading** at [MomentumHeadlineSection], the
 * label is a [MomentumLabelMicro] micro-label, and the row is separated by
 * `Arrangement.SpaceBetween` exactly as the reference's `justify-between`.
 *
 * `Pending` carries a plain `Scheduled Today`. Both status labels take
 * `textPrimary` through [labelColor]. `Completed` carries `Finished` beside the
 * `verified` glyph at [MomentumIconSizes.secondary]; the glyph is tinted by
 * [accent] (Home passes `c.textPrimary`) and is the sole consumer of the shared
 * 2.2 s clock below.
 *
 * **Both labels take `textPrimary`, and the glyph is now ink, not emerald**
 * (ADR-49 Amendment Note 5, `task.md` §4.11 CHANGE 4). Neither label sits on a
 * solid fill — both are bare text over the composited atmosphere — so both opt
 * into `c.textPrimary` through the existing [labelColor] parameter, and the
 * `verified` glyph's [accent] becomes `c.textPrimary` too (the reference's
 * emerald `bolt` measured 2.34:1 in Light, below the 3:1 state-indicator
 * floor). The default `labelColor = null` still resolves to `c.textSecondary`
 * for any future caller. The glyph shape carries the state. `boltPulse` is the
 * screen's shared 2.2 s clock, read inside a `graphicsLayer` block, so only
 * that 20 dp glyph's layer is invalidated per frame and the item never
 * recomposes; `null` rests it on its own 0%/100% frame under reduced motion.
 * The `drop-shadow` half of that keyframe is **not** implemented (a per-frame
 * software shadow on a 20 dp glyph is real cost).
 *
 * @param accent the `verified` glyph's tint, and **nothing else** — the status
 *   label's ink is [labelColor], never the accent. `null` omits the glyph
 *   entirely; Home passes `c.textPrimary`.
 * @param labelColor the status label's ink. `null` keeps `c.textSecondary`; Home
 *   passes `c.textPrimary` for both `Scheduled Today` and `Finished`.
 * @param boltPulse the screen's shared 2.2 s `M4` clock, or `null` under reduced
 *   motion.
 */
@Composable
internal fun HomeSectionHeader(
    title: String,
    meta: String,
    modifier: Modifier = Modifier,
    revealProgress: Float = 1f,
    accent: Color? = null,
    boltPulse: State<Float>? = null,
    labelColor: Color? = null,
) {
    val c = MomentumThemeColors.current
    // ADR-49 Amendment Note 5 (task.md §4.11 CHANGE 4): Home passes
    // `c.textPrimary` for both status bands; `null` still resolves to
    // `c.textSecondary` for any future caller.
    val statusInk = labelColor ?: c.textSecondary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .homeReveal(revealProgress),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MomentumHeadlineSection,
            color = c.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
        ) {
            if (accent != null) {
                Icon(
                    imageVector = MomentumIcons.verified,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier
                        .size(MomentumIconSizes.secondary)
                        .graphicsLayer {
                            val breath = 0.5f - 0.5f * cos((boltPulse?.value ?: 0f) * Tau)
                            scaleX = 1f + VerifiedScale * breath
                            scaleY = 1f + VerifiedScale * breath
                            rotationZ = -VerifiedRotation * breath
                        },
                )
            }
            Text(
                text = meta,
                style = MomentumLabelMicro,
                color = statusInk,
            )
        }
    }
}

/**
 * One Today row: gradient icon tile, title, optional metadata line, completion
 * control — the approved reference's card, with no streak chip and no invented
 * secondary information (§4.7 r4, §8.12 C3).
 *
 * The glass level carries the reference's *relational* structure — `CARD` when
 * pending, `CONTROL` when done, so a completed row is visibly quieter. The rows
 * opt into `fillAlpha = 0.90f` through the Home-scoped `Modifier.liquidGlass`
 * parameter (ADR-49 Amendment Note 5, `task.md` §4.11): this reproduces the
 * rejected global `cardFill/controlFill` raise **at Home's rows only**, lifting
 * the row title/metadata bands over the frozen atmosphere while the ambient
 * Aurora still reads around and between the cards. `MomentumGlass.cardFill/
 * controlFill` stay `0.55/0.45` for every non-Home caller. Its 16 dp radius is
 * `MomentumRadii.card`.
 *
 * A completed row keeps **subdued typography and a strikethrough title**, both
 * expressed with theme tokens only — never by fading the card's own text, which
 * dropped body-text contrast below 4.5:1 in the light theme (DASHBOARD_SPECS
 * §21, AGENTS.md §5).
 *
 * `Modifier.liquidGlass` is called with `hue = null`: the reference paints no
 * chromatic wash on a row card, only on the progress card, and inventing one
 * would be a new visual treatment.
 */
@Composable
internal fun HomeActivityCard(
    item: TodayItem,
    completed: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val activity = item.activity
    val accent = habitIconAccent(activity.iconKey)
    val label = remember(item) { homeMetadataLabel(item) }
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(MomentumRadii.card),
                level = if (completed) GlassLevel.CONTROL else GlassLevel.CARD,
                hue = null,
                // ADR-49 Amendment Note 5 (task.md §4.11): the Home-scoped fill
                // opt-in, reproducing the rejected global cardFill/controlFill
                // 0.90 at Home's rows only. `null` leaves every other card
                // byte-identical.
                fillAlpha = HomeRowFillAlpha,
            )
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onOpen,
            )
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
    ) {
        Box(
            modifier = Modifier
                .size(ActivityTileSize)
                .clipToBounds(),
        ) {
            Box(
                modifier = Modifier
                    .size(ActivityTileSize)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = ActivityTileWashAlpha),
                                accent.copy(alpha = ActivityTileAccentAlpha),
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite,
                        ),
                    )
                    .background(
                        color = Color.White.copy(alpha = ActivityTileInnerAlpha),
                        shape = RoundedCornerShape(MomentumRadii.card),
                    )
                    .border(
                        BorderStroke(
                            Dp.Hairline,
                            accent.copy(alpha = ActivityTileBorderAlpha),
                        ),
                        RoundedCornerShape(MomentumRadii.card),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = momentumIcon(activity.iconKey),
                    contentDescription = null,
                    tint = if (completed) Color.White else accent,
                    modifier = Modifier.size(MomentumIconSizes.primary),
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.micro),
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (completed) c.textSecondary else c.textPrimary,
                textDecoration = if (completed) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            // DECISIONS §29: no metadata row at all when there is no metadata —
            // no placeholder, no reserved line.
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = c.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        CompletionButton(
            completed = completed,
            onToggle = onToggle,
            contentDescription = completionActionLabel(activity, completed),
            // ADR-48 Amendment Note 5: the emerald done fill with its **check**
            // (`code.html:96`, `screen.png`), and the reference's empty **idle**
            // ring — which is the whole of what `showIdleGlyph = false` removes.
            // The done state always draws the glyph (ADR-49 Amendment Note 3,
            // `task.md` §10.8 Ruling A), at the unchanged 48 dp target,
            // `Role.Checkbox`, the existing action label, haptic and pop.
            accent = CompletionAccent.SUCCESS,
            showIdleGlyph = false,
            // ADR-49 Amendment Note 6 (human ruling 2026-10-02 §8.20): Home opts
            // into the more visible idle ring. Every other CompletionButton
            // keeps the shared 0.4 via the `null` default.
            idleRingBorderAlpha = HomeIdleRingBorderAlpha,
        )
    }
}

/**
 * Blob A — the reference's `-top-12 -right-12 w-36 h-36 bg-primary/30` aura, on
 * the screen's shared 6 s `aurora-glow-pulse` clock.
 */
@Composable
private fun AuraBlobA(
    color: Color,
    phase: State<Float>?,
    modifier: Modifier = Modifier,
) {
    AuraBlob(
        color = color,
        diameter = AuraBlobASize,
        phase = phase,
        alpha = AuraBlobAlphaA,
        modifier = modifier,
    )
}

/**
 * Blob B — the reference's `-bottom-10 -left-10 w-32 h-32 bg-secondary/25` aura,
 * on the same clock.
 */
@Composable
private fun AuraBlobB(
    color: Color,
    phase: State<Float>?,
    modifier: Modifier = Modifier,
) {
    AuraBlob(
        color = color,
        diameter = AuraBlobBSize,
        phase = phase,
        alpha = AuraBlobAlphaB,
        modifier = modifier,
    )
}

/**
 * One breathing aura blob.
 *
 * `filter: blur(32px) → blur(40px)` is expressed as an animated
 * **radial-gradient radius** under `canvas.scale`, never as a blur filter
 * (ADR-49 §4, ADR-48 Amendment Note 6 §7): a wider blur radius *is* a wider,
 * softer falloff, which is exactly what a wider radial gradient radius is, and
 * scaling the canvas scales the cached shader's geometry **and** its falloff
 * together. So the animated softness costs **one** brush allocation, zero
 * per-frame allocation, and no new API.
 *
 * The brush is centred on the local origin and the canvas is translated to the
 * blob's centre, exactly as `AuroraBackground` does it. The drawn alpha is the
 * reference's own `opacity: .35 → .6`, multiplying the brush's `bg-primary/30`
 * alpha — the same composition CSS applies.
 */
@Composable
private fun AuraBlob(
    color: Color,
    diameter: Dp,
    alpha: Float,
    phase: State<Float>?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(diameter)
            .drawWithCache {
                val cached = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                    center = Offset.Zero,
                    radius = AuraBlobRadius.toPx(),
                )
                val blobRadius = AuraBlobRadius.toPx()
                onDrawBehind {
                    val breath = 0.5f - 0.5f * cos((phase?.value ?: 0f) * Tau)
                    val canvas = drawContext.canvas
                    canvas.save()
                    canvas.translate(size.width / 2f, size.height / 2f)
                    canvas.scale(1f + AuraRadiusBreath * breath)
                    drawCircle(
                        brush = cached,
                        radius = blobRadius,
                        center = Offset.Zero,
                        alpha = lerp(AuraAlphaRest, AuraAlphaPeak, breath),
                    )
                    canvas.restore()
                }
            },
    )
}

/**
 * The approved entrance reveal, expressed as one layer transform: the
 * reference's `fade-slide-up` is `opacity 0 → 1` with `translateY(14px → 0)`, and
 * a `graphicsLayer` is its continuous Compose equivalent. It is driven by the
 * screen's single [MomentumMotion.enter] clock and read **inside** the layer
 * block, so nothing here owns an animation instance.
 *
 * The reference's per-block stagger (0 / 100 / 200 / 300 ms) is resolved by the
 * screen, which derives four phases from that one clock; **activity cards carry
 * none** — a `LazyColumn` row has no "first composition" identity, so a per-item
 * entrance would re-fire on every scroll-in.
 */
internal fun Modifier.homeReveal(reveal: Float): Modifier = graphicsLayer {
    val p = reveal.coerceIn(0f, 1f)
    alpha = p
    translationY = (1f - p) * EntranceTravel.toPx()
}

/**
 * Layout-contract tag for the progress card. It carries no accessibility meaning
 * beyond the merged description the card already owns; it exists so the
 * composition (the horizontal lockup) can be asserted at bounds level.
 */
internal const val HomeProgressCardTag = "homeProgressCard"

/** The reference's `translateY(14px)` entrance travel. */
private val EntranceTravel = 14.dp

/** The reference's `w-36` / `w-32` blob diameters. */
private val AuraBlobASize = 144.dp
private val AuraBlobBSize = 128.dp

/** The reference's `-top-12 -right-12` and `-bottom-10 -left-10` overhangs. */
private val AuraBlobAOverhang = 48.dp
private val AuraBlobBOverhang = 40.dp

/** The reference's `blur(32px)` resting radius; the peak is this + 25 %. */
private val AuraBlobRadius = 32.dp

/** `32px -> 40px` over one cycle, as the canvas scale that widens the falloff. */
private const val AuraRadiusBreath = 0.25f

/** The reference's `bg-primary/30` and `bg-secondary/25`. */
private const val AuraBlobAlphaA = 0.30f
private const val AuraBlobAlphaB = 0.25f

/** The reference's own `opacity: .35 -> .6` keyframe stops. */
private const val AuraAlphaRest = 0.35f
private const val AuraAlphaPeak = 0.60f

/** 2π, for the blobs' and the glyph's breathing phase. */
private const val Tau = 6.2831853f

/** The reference's `bolt-pulse` peak: `scale(1.15) rotate(-4deg)`. */
private const val VerifiedScale = 0.15f
private const val VerifiedRotation = 4f

/** The reference's breakdown dots. */
private val BreakdownDotSize = 8.dp

/** The reference's card divider, `rgba(15,23,42,0.08)` over the glass. */
private const val DividerAlpha = 0.08f

/** The reference's `w-12 h-12` icon tile. */
private val ActivityTileSize = 48.dp

/**
 * The rejected global E3 fill raises, re-applied to Home only through
 * `Modifier.liquidGlass(fillAlpha = …)` (ADR-49 Amendment Note 5, `task.md`
 * §4.11). `MomentumGlass.heroFill/cardFill/controlFill` stay `0.68/0.55/0.45`
 * for every non-Home caller.
 */
private const val ProgressCardFillAlpha = 0.85f
private const val HomeRowFillAlpha = 0.90f

/** Home's opt-in idle-ring border alpha (ADR-49 Amendment Note 6, §8.20). */
private const val HomeIdleRingBorderAlpha = 0.8f

/** The reference's 135° two-stop tile gradient and its `bg-white/20` inner wash. */
private const val ActivityTileWashAlpha = 0.20f
private const val ActivityTileAccentAlpha = 0.42f
private const val ActivityTileInnerAlpha = 0.20f
private const val ActivityTileBorderAlpha = 0.34f
