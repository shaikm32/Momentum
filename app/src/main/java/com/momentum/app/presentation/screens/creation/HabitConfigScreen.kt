package com.momentum.app.presentation.screens.creation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.ReduceMode
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.ViewModelArgs
import com.momentum.app.presentation.hiltViewModelWithArgs
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.HabitIconAccents
import com.momentum.app.presentation.theme.MomentumBodyStrong
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import com.momentum.app.presentation.theme.MomentumThemeColors

/**
 * Unified habit creation screen (single scroll): intent cards → template
 * picker → habit details → fixed Create CTA. In edit mode the intent and
 * template are locked to the persisted values and only the details below
 * are editable, so an edit can never reset the selected mode.
 */
@Composable
fun HabitConfigRoute(
    activityId: Long?,
    intent: HabitIntent,
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: CreationViewModel = hiltViewModelWithArgs {
        putString(ViewModelArgs.ACTIVITY_ID, activityId?.toString())
        putString(ViewModelArgs.ACTIVITY_TYPE, ActivityType.HABIT.name)
        putString(ViewModelArgs.HABIT_INTENT, intent.name)
    }
    val form by vm.form.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is CreationEffect.Saved -> onSaved(effect.activityId)
                is CreationEffect.Error -> snackbar.showSnackbar(effect.message.resolve(context))
            }
        }
    }
    HabitConfigScreen(
        form = form,
        categories = categories,
        onUpdate = { vm.onEvent(CreationEvent.Update(it)) },
        onSelectIntent = { vm.onEvent(CreationEvent.SelectIntent(it)) },
        onSelectTemplate = { vm.onEvent(CreationEvent.SelectTemplate(it)) },
        onSave = { vm.onEvent(CreationEvent.Save) },
        onBack = onBack,
    )
}

@Composable
fun HabitConfigScreen(
    form: CreationForm,
    categories: List<Category>,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    onSelectIntent: (HabitIntent) -> Unit,
    onSelectTemplate: (HabitTemplate) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val reduceMotion = rememberReduceMotion(null)
    val subtitle = when (form.habitIntent) {
        HabitIntent.BUILD -> stringResource(R.string.habit_subtitle_build)
        HabitIntent.REDUCE -> stringResource(R.string.habit_subtitle_reduce)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        MomentumTopBar(
            title = if (form.isNew) stringResource(R.string.habit_title_new)
            else stringResource(R.string.habit_title_edit),
            subtitle = subtitle,
            onBack = onBack,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = MomentumSpacing.screen,
                end = MomentumSpacing.screen,
                top = MomentumSpacing.group,
                // Clearance above the fixed CTA bar so it never covers content.
                bottom = MomentumSpacing.screen,
            ),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
        ) {
            if (form.isNew) {
                item {
                    MomentumSectionHeader(title = stringResource(R.string.habit_section_intent))
                }
                item {
                    IntentCards(
                        selected = form.habitIntent,
                        onPick = onSelectIntent,
                    )
                }
                item {
                    MomentumSectionHeader(title = stringResource(R.string.habit_section_choose))
                }
                // Locked 2-column grid: equal-width cards, spacing mirrors the
                // LazyColumn row gap so columns and rows share one rhythm.
                templatesFor(form.habitIntent).chunked(2).forEach { row ->
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            row.forEach { template ->
                                TemplateCard(
                                    template = template,
                                    selected = form.templateId == template.id,
                                    onClick = { onSelectTemplate(template) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                item {
                    LockedIntentSummary(form = form)
                }
            }
            item {
                val selectedTemplate = templateById(form.habitIntent, form.templateId)
                NameAndIconSection(
                    form = form,
                    onUpdate = onUpdate,
                    compactIcon = true,
                    // Change Icon only belongs to the free-form Custom habit;
                    // predefined templates own their icon, so the control is
                    // hidden while one of them is selected.
                    showIconEditor = selectedTemplate?.isCustom == true,
                )
            }
            item {
                HabitMeasureSection(form = form, onUpdate = onUpdate, reduceMotion = reduceMotion)
            }
            item {
                ScheduleSection(
                    form = form,
                    onUpdate = onUpdate,
                    reduceMotion = reduceMotion,
                    anchorDate = form.startDate,
                    onAnchorDate = { d -> onUpdate { it.copy(startDate = d) } },
                )
            }
            item {
                ReminderSection(form = form, onUpdate = onUpdate, reduceMotion = reduceMotion)
            }
            item {
                MomentumSectionHeader(title = stringResource(R.string.section_details_optional))
            }
            item {
                NotesSection(form = form, onUpdate = onUpdate)
            }
            item {
                CategorySection(form = form, categories = categories, onUpdate = onUpdate)
            }
            item {
                PrioritySection(form = form, onUpdate = onUpdate)
            }
            item {
                Spacer(Modifier.height(MomentumSpacing.group))
            }
        }
        CreateCtaBar(
            text = when {
                form.isSaving -> stringResource(R.string.action_saving)
                form.isNew -> stringResource(R.string.habit_config_create)
                else -> stringResource(R.string.action_save_changes)
            },
            enabled = !form.isSaving && form.isSubmittable(),
            onClick = onSave,
        )
    }
}

/**
 * Selection ring over the existing card treatment: same glass surface, with a
 * tinted ring when selected. Ring width is constant so selection never shifts
 * layout. The glass column fills the fixed-height box so compact cards read as
 * one intentional surface; padding/arrangement are parameters so intent and
 * template cards can tune internal rhythm without clipping.
 */
@Composable
private fun SelectionRing(
    selected: Boolean,
    onClickLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(MomentumSpacing.card),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(MomentumSpacing.group),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = MomentumThemeColors.current
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(MomentumRadii.card)
    Box(
        modifier = modifier
            .clip(shape)
            .border(
                BorderStroke(
                    2.dp,
                    if (selected) c.primary.copy(alpha = 0.9f) else Color.Transparent,
                ),
                shape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            )
            // One announcement per card: its own texts and decorative accent
            // badge are merged into the clickable node instead of being read
            // again after it.
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .liquidGlass(shape, GlassLevel.CARD)
                .padding(contentPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }
}

@Composable
private fun IntentCards(
    selected: HabitIntent,
    onPick: (HabitIntent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        modifier = Modifier.fillMaxWidth(),
    ) {
        IntentCard(
            icon = Icons.AutoMirrored.Rounded.TrendingUp,
            accent = HabitIconAccents.peach,
            title = stringResource(R.string.intent_build),
            body = stringResource(R.string.intent_build_body),
            selected = selected == HabitIntent.BUILD,
            onClick = { onPick(HabitIntent.BUILD) },
            modifier = Modifier
                .weight(1f)
                .height(INTENT_CARD_HEIGHT),
        )
        IntentCard(
            icon = Icons.AutoMirrored.Rounded.TrendingDown,
            accent = HabitIconAccents.plum,
            title = stringResource(R.string.intent_reduce),
            body = stringResource(R.string.intent_reduce_body),
            selected = selected == HabitIntent.REDUCE,
            onClick = { onPick(HabitIntent.REDUCE) },
            modifier = Modifier
                .weight(1f)
                .height(INTENT_CARD_HEIGHT),
        )
    }
}

/**
 * Intent selector tile in the same card language as [TemplateCard]: accent
 * badge on the left, title + supporting copy stacked to its right, icon
 * top-aligned with the title. Fixed height (caller) keeps both
 * intent cards identical; description never drives layout.
 */
@Composable
private fun IntentCard(
    icon: ImageVector,
    accent: Color,
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    SelectionRing(
        selected = selected,
        onClickLabel = title,
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(MomentumSpacing.card),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AccentBadge(accent = accent, icon = icon)
            Column(
                verticalArrangement = Arrangement.spacedBy(INTENT_TEXT_GAP),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    title,
                    style = MomentumBodyStrong,
                    color = c.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    body,
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val INTENT_CARD_HEIGHT = 104.dp
private val INTENT_TEXT_GAP = 6.dp

/**
 * One consistent icon treatment across the whole screen: rounded soft badge
 * with the accent at low alpha, glyph in the accent hue. Same shape language
 * everywhere; only the hue changes per habit.
 */
@Composable
private fun AccentBadge(
    accent: Color,
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(MomentumIconSizes.tile),
        )
    }
}

/** Accent hue per template; distinct within each intent grid, Twilight-calm throughout. */
private fun templateAccent(templateId: String): Color = when (templateId) {
    "build_read" -> HabitIconAccents.amber
    "build_exercise" -> HabitIconAccents.teal
    "build_meditate" -> HabitIconAccents.lavender
    "build_water" -> HabitIconAccents.cyan
    "build_learn" -> HabitIconAccents.golden
    "reduce_social" -> HabitIconAccents.plum
    "reduce_sugar" -> HabitIconAccents.amber
    "reduce_spending" -> HabitIconAccents.golden
    "reduce_screentime" -> HabitIconAccents.cyan
    else -> HabitIconAccents.peach
}

/**
 * Presentation-only display labels for the 2×3 picker grid. Shorter than the
 * template `defaultTitle` values, which stay untouched literals so selection,
 * pre-filled names, and edit-mode matching keep working unchanged.
 */
@Composable
private fun templateDisplayLabel(template: HabitTemplate): String =
    stringResource(template.labelRes)

/**
 * Compact predefined-habit tile: fixed [TEMPLATE_CARD_HEIGHT] so every card in
 * the locked 2×3 grid shares identical dimensions. Horizontal composition —
 * accent badge as the visual identifier, the short title beside it top-aligned
 * with the badge (never driving height). The whole glass card is the tap
 * target; no trailing chevron.
 */
@Composable
private fun TemplateCard(
    template: HabitTemplate,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MomentumThemeColors.current
    val accent = templateAccent(template.id)
    val displayLabel = templateDisplayLabel(template)
    SelectionRing(
        selected = selected,
        onClickLabel = displayLabel,
        onClick = onClick,
        modifier = modifier.height(TEMPLATE_CARD_HEIGHT),
        contentPadding = PaddingValues(horizontal = MomentumSpacing.row, vertical = MomentumSpacing.row),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AccentBadge(accent = accent, icon = momentumIcon(template.iconKey))
            Text(
                displayLabel,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = c.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Fixed compact tile height — equal for all templates, independent of copy length. */
private val TEMPLATE_CARD_HEIGHT = 76.dp

/** Edit mode: persisted intent/mode shown as context (never editable here). */
@Composable
private fun LockedIntentSummary(form: CreationForm) {
    val text = when (form.habitIntent) {
        HabitIntent.BUILD -> stringResource(R.string.locked_build_summary)
        HabitIntent.REDUCE ->
            if (form.reduceMode == ReduceMode.COMPLETE) {
                stringResource(R.string.locked_reduce_zero_summary)
            } else {
                stringResource(R.string.locked_reduce_limit_summary)
            }
    }
    MomentumCard {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MomentumThemeColors.current.textSecondary)
    }
}

@Composable
private fun HabitMeasureSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
) {
    when (form.habitIntent) {
        HabitIntent.BUILD -> {
            MomentumSectionHeader(title = stringResource(R.string.measure_build_section))
            MomentumCard {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CategoryChip(
                        label = stringResource(R.string.chip_just_mark_done),
                        selected = form.buildBinary,
                        onClick = { onUpdate { it.copy(buildBinary = true, measureError = null) } },
                        modifier = Modifier.weight(1f),
                    )
                    CategoryChip(
                        label = stringResource(R.string.chip_reach_target),
                        selected = !form.buildBinary,
                        onClick = { onUpdate { it.copy(buildBinary = false, measureError = null) } },
                        modifier = Modifier.weight(1f),
                    )
                }
                Expandable(visible = !form.buildBinary, reduceMotion = reduceMotion) {
                    AmountUnitFields(
                        form = form,
                        onUpdate = onUpdate,
                        amountLabel = stringResource(R.string.habit_tracking_target_amount),
                    )
                }
            }
        }
        HabitIntent.REDUCE -> {
            MomentumSectionHeader(title = stringResource(R.string.measure_reduce_section))
            MomentumCard {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CategoryChip(
                        label = stringResource(R.string.chip_set_limit),
                        selected = form.reduceMode == ReduceMode.LIMIT,
                        onClick = { onUpdate { it.copy(reduceMode = ReduceMode.LIMIT, measureError = null) } },
                        modifier = Modifier.weight(1f),
                    )
                    CategoryChip(
                        label = stringResource(R.string.habit_tracking_completely),
                        selected = form.reduceMode == ReduceMode.COMPLETE,
                        onClick = { onUpdate { it.copy(reduceMode = ReduceMode.COMPLETE, measureError = null) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Expandable(visible = form.reduceMode == ReduceMode.LIMIT, reduceMotion = reduceMotion) {
                MomentumCard {
                    Explainer(stringResource(R.string.limit_explainer))
                    AmountUnitFields(
                        form = form,
                        onUpdate = onUpdate,
                        amountLabel = stringResource(R.string.amount_label_limit),
                    )
                }
            }
            Expandable(visible = form.reduceMode == ReduceMode.COMPLETE, reduceMotion = reduceMotion) {
                MomentumCard {
                    Explainer(stringResource(R.string.zero_explainer))
                }
            }
        }
    }
    form.measureError?.let {
        Text(
            it.resolve(),
            style = MaterialTheme.typography.bodySmall,
            color = MomentumThemeColors.current.error,
            // The message appears after the tap that caused it, so it is a live
            // region: without one, TalkBack stays silent on an invalid save.
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
}

/**
 * Amount + unit editor with clearly separated regions: amount field, unit
 * label + preset chips, then a custom-unit field. Shared by Build-target
 * and Reduce-limit habits.
 */
@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun AmountUnitFields(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    amountLabel: String,
) {
    val c = MomentumThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
        OutlinedTextField(
            value = form.amount,
            onValueChange = { v -> onUpdate { it.copy(amount = v, measureError = null) } },
            label = { Text(amountLabel) },
            placeholder = { Text(stringResource(R.string.amount_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(MomentumRadii.control),
            colors = glassFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(R.string.unit_label),
            style = MaterialTheme.typography.labelLarge,
            color = c.textSecondary,
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            SupportedUnits.forEach { unit ->
                CategoryChip(
                    label = unit,
                    selected = form.unit == unit,
                    onClick = { onUpdate { it.copy(unit = unit, measureError = null) } },
                )
            }
        }
        OutlinedTextField(
            value = if (form.unit in SupportedUnits) "" else form.unit,
            onValueChange = { v -> onUpdate { it.copy(unit = v, measureError = null) } },
            label = { Text(stringResource(R.string.custom_unit_label)) },
            placeholder = { Text(stringResource(R.string.custom_unit_placeholder)) },
            singleLine = true,
            shape = RoundedCornerShape(MomentumRadii.control),
            colors = glassFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "Habit create Build — light", showBackground = true)
@Composable
private fun HabitCreateBuildPreview() {
    MomentumTheme(themeId = "twilight", darkMode = DarkModeSetting.LIGHT) {
        HabitConfigScreen(
            form = CreationForm(
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.BUILD,
                title = "Read before bed",
            ),
            categories = emptyList(),
            onUpdate = {},
            onSelectIntent = {},
            onSelectTemplate = {},
            onSave = {},
            onBack = {},
        )
    }
}

@Preview(name = "Habit create Reduce — dark", showBackground = true)
@Composable
private fun HabitCreateReducePreview() {
    MomentumTheme(themeId = "twilight", darkMode = DarkModeSetting.DARK) {
        HabitConfigScreen(
            form = CreationForm(
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.REDUCE,
                reduceMode = ReduceMode.LIMIT,
                title = "Social media",
                amount = "60",
                unit = "min",
                templateId = "reduce_social",
            ),
            categories = emptyList(),
            onUpdate = {},
            onSelectIntent = {},
            onSelectTemplate = {},
            onSave = {},
            onBack = {},
        )
    }
}

@Preview(name = "Habit reduce Completely — dark", showBackground = true)
@Composable
private fun HabitReduceCompletePreview() {
    MomentumTheme(themeId = "twilight", darkMode = DarkModeSetting.DARK) {
        HabitConfigScreen(
            form = CreationForm(
                activityType = ActivityType.HABIT,
                habitIntent = HabitIntent.REDUCE,
                reduceMode = ReduceMode.COMPLETE,
                title = "Smoking",
                templateId = CUSTOM_REDUCE_TEMPLATE_ID,
            ),
            categories = emptyList(),
            onUpdate = {},
            onSelectIntent = {},
            onSelectTemplate = {},
            onSave = {},
            onBack = {},
        )
    }
}
