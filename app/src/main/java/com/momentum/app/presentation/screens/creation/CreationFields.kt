package com.momentum.app.presentation.screens.creation

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.momentum.app.R
import com.momentum.app.domain.model.Category
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumIconOptions
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.resolve
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Translucent text-field colors shared by every field in the creation flow. */
@Composable
internal fun glassFieldColors(): androidx.compose.material3.TextFieldColors {
    val c = MomentumThemeColors.current
    return OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = c.surface.copy(alpha = 0.45f),
        focusedContainerColor = c.surface.copy(alpha = 0.65f),
        unfocusedBorderColor = c.textSecondary.copy(alpha = 0.25f),
        focusedBorderColor = c.primary.copy(alpha = 0.65f),
        cursorColor = c.primary,
    )
}

@Composable
internal fun Expandable(
    visible: Boolean,
    reduceMotion: Boolean,
    content: @Composable () -> Unit,
) {
    if (reduceMotion) {
        if (visible) content()
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            content()
        }
    }
}

@Composable
internal fun Explainer(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MomentumThemeColors.current.textSecondary,
        modifier = modifier,
    )
}

@Composable
internal fun IconChoice(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val c = MomentumThemeColors.current
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(MomentumRadii.small))
            .background(
                if (selected) {
                    c.primary.copy(alpha = 0.85f)
                } else {
                    c.surface.copy(alpha = MomentumGlass.controlFill)
                },
            )
            .border(
                BorderStroke(
                    Dp.Hairline,
                    if (selected) {
                        Color.White.copy(alpha = 0.3f)
                    } else {
                        c.textSecondary.copy(alpha = 0.22f)
                    },
                ),
                RoundedCornerShape(MomentumRadii.small),
            )
            .glassPress(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClickLabel = label,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
internal fun LabeledSwitch(
    label: String,
    supporting: String?,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val c = MomentumThemeColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.row),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
            if (supporting != null) {
                Text(supporting, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

/** Step 1 for every kind: name (required) + icon. */
@Composable
internal fun NameAndIconSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    /**
     * When true, shows a compact "[icon] Change icon >" row that opens the
     * full library in a dialog instead of the large inline grid. Habits use
     * the compact control; tasks keep the inline grid unchanged.
     */
    compactIcon: Boolean = false,
    /**
     * Presentation visibility for the icon editor. Habits pass false while a
     * predefined template is selected (the template owns the icon); the Custom
     * habit and every other flow keep the editor exactly as before.
     */
    showIconEditor: Boolean = true,
) {
    var pickerOpen by remember { mutableStateOf(false) }
    MomentumSectionHeader(title = stringResource(R.string.section_what_is_it))
    OutlinedTextField(
        value = form.title,
        onValueChange = { v -> onUpdate { it.copy(title = v, titleError = null) } },
        label = { Text(stringResource(R.string.field_name)) },
        placeholder = { Text(stringResource(R.string.field_name_placeholder)) },
        singleLine = true,
        isError = form.titleError != null,
        supportingText = { form.titleError?.let { Text(it.resolve()) } },
        shape = RoundedCornerShape(MomentumRadii.control),
        colors = glassFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )
    if (showIconEditor) {
        if (compactIcon) {
            CompactIconRow(
                iconKey = form.iconKey,
                onOpenPicker = { pickerOpen = true },
            )
        } else {
            MomentumCard {
                Text(
                    stringResource(R.string.field_icon),
                    style = MaterialTheme.typography.titleMedium,
                    color = MomentumThemeColors.current.textPrimary,
                )
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                    verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                ) {
                    MomentumIconOptions.forEach { option ->
                        IconChoice(
                            selected = form.iconKey == option.key,
                            label = stringResource(option.labelRes),
                            onClick = { onUpdate { it.copy(iconKey = option.key) } },
                        ) {
                            Icon(
                                momentumIcon(option.key),
                                contentDescription = null,
                                tint = if (form.iconKey == option.key) {
                                    MomentumThemeColors.current.onPrimary
                                } else {
                                    MomentumThemeColors.current.textSecondary
                                },
                                modifier = Modifier.size(MomentumIconSizes.medium),
                            )
                        }
                    }
                }
            }
        }
    }
    if (pickerOpen && showIconEditor) {
        IconPickerDialog(
            selectedKey = form.iconKey,
            onPick = { key ->
                onUpdate { it.copy(iconKey = key) }
                pickerOpen = false
            },
            onDismiss = { pickerOpen = false },
        )
    }
}

/** Compact icon control: selected icon + "Change icon" affordance. */
@Composable
private fun CompactIconRow(
    iconKey: String,
    onOpenPicker: () -> Unit,
) {
    val c = MomentumThemeColors.current
    val interaction = remember { MutableInteractionSource() }
    val selectedLabel = MomentumIconOptions.firstOrNull { it.key == iconKey }
        ?.let { stringResource(it.labelRes) }
        ?: stringResource(R.string.field_icon)
    MomentumCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.card),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MomentumRadii.control))
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClickLabel = stringResource(R.string.action_change_icon),
                    onClick = onOpenPicker,
                )
                // One announcement for the row: the tile glyph is decorative and
                // the two texts merge into the clickable node.
                .semantics(mergeDescendants = true) {},
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(MomentumRadii.small))
                    .background(c.primary.copy(alpha = 0.85f))
                    .border(
                        BorderStroke(Dp.Hairline, Color.White.copy(alpha = 0.3f)),
                        RoundedCornerShape(MomentumRadii.small),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    momentumIcon(iconKey),
                    contentDescription = null,
                    tint = c.onPrimary,
                    modifier = Modifier.size(MomentumIconSizes.medium),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(selectedLabel, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
                Text(
                    stringResource(R.string.action_change_icon),
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.primary,
                )
            }
        }
    }
}

/** Full icon library in a dialog; closes on selection or dismiss. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconPickerDialog(
    selectedKey: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val c = MomentumThemeColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(MomentumRadii.dialog),
        containerColor = c.surface.copy(alpha = 0.95f),
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_done)) }
        },
        title = { Text(stringResource(R.string.icon_picker_title)) },
        text = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            ) {
                MomentumIconOptions.forEach { option ->
                    IconChoice(
                        selected = selectedKey == option.key,
                        label = stringResource(option.labelRes),
                        onClick = { onPick(option.key) },
                    ) {
                        Icon(
                            momentumIcon(option.key),
                            contentDescription = null,
                            tint = if (selectedKey == option.key) {
                                MomentumThemeColors.current.onPrimary
                            } else {
                                MomentumThemeColors.current.textSecondary
                            },
                            modifier = Modifier.size(MomentumIconSizes.medium),
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CategorySection(
    form: CreationForm,
    categories: List<Category>,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
) {
    if (categories.isEmpty()) return
    MomentumCard {
        Text(
            stringResource(R.string.field_category),
            style = MaterialTheme.typography.titleMedium,
            color = MomentumThemeColors.current.textPrimary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            CategoryChip(
                label = stringResource(R.string.category_none),
                selected = form.categoryId == null,
                onClick = { onUpdate { it.copy(categoryId = null) } },
            )
            categories.forEach { cat ->
                CategoryChip(
                    label = cat.name,
                    selected = form.categoryId == cat.id,
                    onClick = {
                        onUpdate {
                            it.copy(categoryId = if (it.categoryId == cat.id) null else cat.id)
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun PrioritySection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
) {
    MomentumCard {
        Text(
            stringResource(R.string.field_priority),
            style = MaterialTheme.typography.titleMedium,
            color = MomentumThemeColors.current.textPrimary,
        )
        Explainer(stringResource(R.string.priority_explainer))
        Row(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            modifier = Modifier.fillMaxWidth(),
        ) {
            listOf(
                stringResource(R.string.priority_normal),
                stringResource(R.string.priority_medium),
                stringResource(R.string.priority_high),
                stringResource(R.string.priority_top),
            ).forEachIndexed { p, label ->
                CategoryChip(
                    label = label,
                    selected = form.priority == p,
                    onClick = { onUpdate { it.copy(priority = p) } },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun NotesSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
) {
    OutlinedTextField(
        value = form.notes,
        onValueChange = { v -> onUpdate { it.copy(notes = v) } },
        label = { Text(stringResource(R.string.field_notes)) },
        placeholder = { Text(stringResource(R.string.field_notes_placeholder)) },
        shape = RoundedCornerShape(MomentumRadii.control),
        colors = glassFieldColors(),
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
    )
}

@Composable
internal fun ReminderSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
    supporting: String = stringResource(R.string.reminder_supporting_default),
) {
    MomentumSectionHeader(title = stringResource(R.string.section_reminder))
    MomentumCard {
        LabeledSwitch(
            label = stringResource(R.string.reminder_nudge),
            supporting = supporting,
            checked = form.reminderEnabled,
            onChange = { checked -> onUpdate { it.copy(reminderEnabled = checked) } },
        )
        Expandable(visible = form.reminderEnabled, reduceMotion = reduceMotion) {
            TimeRow(
                label = stringResource(R.string.reminder_time_label),
                time = form.reminderTime,
                onPick = { t -> onUpdate { it.copy(reminderTime = t) } },
            )
        }
    }
}

/**
 * Schedule editor: simple presets first, full recurrence only under Custom.
 * Habits bind [anchorDate]/[onAnchorDate] to the start date and show the end
 * date; repeating tasks bind the anchor to the due date and hide both rows
 * (the anchor is edited in the Due section, repeats run indefinitely).
 */
@Composable
internal fun ScheduleSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
    anchorDate: LocalDate,
    onAnchorDate: (LocalDate) -> Unit,
    showAnchorDate: Boolean = true,
    showEndDate: Boolean = true,
    /** When true, renders the fields inline (for embedding in another card). */
    bare: Boolean = false,
) {
    if (!bare) {
        MomentumSectionHeader(title = stringResource(R.string.section_schedule))
    }
    val presets = listOf(
        stringResource(R.string.preset_every_day),
        stringResource(R.string.preset_weekdays),
        stringResource(R.string.preset_specific_days),
        stringResource(R.string.preset_custom),
    )
    val selected = when (form.recurrenceChoice) {
        RecurrenceChoice.DAILY -> 0
        RecurrenceChoice.WEEKDAYS -> 1
        RecurrenceChoice.CUSTOM_DAYS -> 2
        else -> 3
    }
    if (bare) {
        Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
            ScheduleBody(
                form = form,
                onUpdate = onUpdate,
                reduceMotion = reduceMotion,
                presets = presets,
                selectedPreset = selected,
                anchorDate = anchorDate,
                onAnchorDate = onAnchorDate,
                showAnchorDate = showAnchorDate,
                showEndDate = showEndDate,
            )
        }
    } else {
        MomentumCard {
            ScheduleBody(
                form = form,
                onUpdate = onUpdate,
                reduceMotion = reduceMotion,
                presets = presets,
                selectedPreset = selected,
                anchorDate = anchorDate,
                onAnchorDate = onAnchorDate,
                showAnchorDate = showAnchorDate,
                showEndDate = showEndDate,
            )
        }
    }
}

@Composable
private fun ScheduleBody(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
    presets: List<String>,
    selectedPreset: Int,
    anchorDate: LocalDate,
    onAnchorDate: (LocalDate) -> Unit,
    showAnchorDate: Boolean,
    showEndDate: Boolean,
) {
    val selected = selectedPreset
    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            presets.forEachIndexed { index, label ->
                CategoryChip(
                    label = label,
                    selected = selected == index,
                    onClick = {
                        onUpdate {
                            it.copy(
                                recurrenceChoice = when (index) {
                                    0 -> RecurrenceChoice.DAILY
                                    1 -> RecurrenceChoice.WEEKDAYS
                                    2 -> RecurrenceChoice.CUSTOM_DAYS
                                    else -> when (it.recurrenceChoice) {
                                        RecurrenceChoice.DAILY,
                                        RecurrenceChoice.WEEKDAYS,
                                        RecurrenceChoice.CUSTOM_DAYS,
                                        -> RecurrenceChoice.EVERY_N_DAYS
                                        else -> it.recurrenceChoice
                                    }
                                },
                            )
                        }
                    },
                )
            }
        }
        Expandable(
            visible = form.recurrenceChoice == RecurrenceChoice.CUSTOM_DAYS ||
                form.recurrenceChoice == RecurrenceChoice.EVERY_N_WEEKS,
            reduceMotion = reduceMotion,
        ) {
            WeekdayPicker(
                selected = form.days,
                onToggle = { day ->
                    onUpdate { it.copy(days = if (day in it.days) it.days - day else it.days + day) }
                },
            )
        }
        Expandable(visible = selected == 3, reduceMotion = reduceMotion) {
            AdvancedRecurrenceRow(form = form, onUpdate = onUpdate)
        }
        Expandable(
            visible = form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS ||
                form.recurrenceChoice == RecurrenceChoice.EVERY_N_WEEKS,
            reduceMotion = reduceMotion,
        ) {
            Stepper(
                label = if (form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS) {
                    stringResource(R.string.stepper_every_days, form.interval)
                } else {
                    stringResource(R.string.stepper_every_weeks, form.interval)
                },
                value = form.interval,
                range = 1..30,
                onChange = { v -> onUpdate { it.copy(interval = v) } },
            )
        }
        Expandable(
            visible = form.recurrenceChoice == RecurrenceChoice.MONTHLY,
            reduceMotion = reduceMotion,
        ) {
            Stepper(
                label = stringResource(R.string.stepper_day_of_month, form.dayOfMonth),
                value = form.dayOfMonth,
                range = 1..31,
                onChange = { v -> onUpdate { it.copy(dayOfMonth = v) } },
            )
        }
        if (showAnchorDate) {
            DateRow(
                label = stringResource(R.string.field_starts),
                date = anchorDate,
                onPick = onAnchorDate,
            )
        }
        if (showEndDate) {
            LabeledSwitch(
                label = stringResource(R.string.field_end_date),
                supporting = if (form.hasEndDate) stringResource(R.string.field_stops_repeating)
                else stringResource(R.string.field_repeats_indefinitely),
                checked = form.hasEndDate,
                onChange = { checked -> onUpdate { it.copy(hasEndDate = checked) } },
            )
            Expandable(visible = form.hasEndDate, reduceMotion = reduceMotion) {
                DateRow(
                    label = stringResource(R.string.field_ends),
                    date = form.endDate,
                    onPick = { d -> onUpdate { it.copy(endDate = d) } },
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdvancedRecurrenceRow(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val c = MomentumThemeColors.current
    val labels = mapOf(
        RecurrenceChoice.EVERY_N_DAYS to stringResource(R.string.pattern_every_n_days),
        RecurrenceChoice.WEEKLY to stringResource(R.string.pattern_weekly),
        RecurrenceChoice.EVERY_N_WEEKS to stringResource(R.string.pattern_every_n_weeks),
        RecurrenceChoice.MONTHLY to stringResource(R.string.pattern_monthly),
        RecurrenceChoice.ONCE to stringResource(R.string.pattern_once),
    )
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = labels[form.recurrenceChoice] ?: stringResource(R.string.pattern_every_n_days),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.field_repeat_pattern)) },
            shape = RoundedCornerShape(MomentumRadii.control),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                c.surface.copy(alpha = 0.95f),
                RoundedCornerShape(MomentumRadii.control),
            ),
        ) {
            labels.forEach { (choice, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onUpdate { it.copy(recurrenceChoice = choice) }; expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WeekdayPicker(selected: Set<DayOfWeek>, onToggle: (DayOfWeek) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        DayOfWeek.entries.forEach { day ->
            CategoryChip(
                label = day.name.lowercase().replaceFirstChar(Char::titlecase).take(3),
                selected = day in selected,
                onClick = { onToggle(day) },
            )
        }
    }
}

@Composable
internal fun Stepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    val c = MomentumThemeColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = c.textPrimary,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onChange((value - 1).coerceIn(range)) }) {
            Icon(Icons.Rounded.Remove, contentDescription = stringResource(R.string.cd_decrease))
        }
        Text(
            "$value",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = c.textPrimary,
        )
        IconButton(onClick = { onChange((value + 1).coerceIn(range)) }) {
            Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.cd_increase))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRow(label: String, date: LocalDate, onPick: (LocalDate) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val c = MomentumThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.control),
                GlassLevel.CONTROL,
            )
            .clickable(onClickLabel = label) { open = true }
            // One announcement for the row: label + value merge into the node.
            .semantics(mergeDescendants = true) {}
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = c.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            date.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy")),
            color = c.textPrimary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    if (open) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            shape = RoundedCornerShape(MomentumRadii.dialog),
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                containerColor = c.surface.copy(alpha = 0.92f),
            ),
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onPick(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    open = false
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeRow(label: String, time: String, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val c = MomentumThemeColors.current
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.control),
                GlassLevel.CONTROL,
            )
            .clickable(onClickLabel = label) { open = true }
            // One announcement for the row: label + value merge into the node.
            .semantics(mergeDescendants = true) {}
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = c.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(time, color = c.textPrimary, style = MaterialTheme.typography.titleMedium)
    }
    if (open) {
        val parts = time.split(":")
        val state = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 8,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = DateFormat.is24HourFormat(context),
        )
        AlertDialog(
            onDismissRequest = { open = false },
            shape = RoundedCornerShape(MomentumRadii.dialog),
            containerColor = c.surface.copy(alpha = 0.92f),
            confirmButton = {
                TextButton(onClick = {
                    onPick("%02d:%02d".format(state.hour, state.minute))
                    open = false
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text(stringResource(R.string.action_cancel)) }
            },
            text = { TimePicker(state = state) },
        )
    }
}

/** Bottom CTA bar shared by every config screen: enabled state reflects validity. */
@Composable
internal fun CreateCtaBar(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                RoundedCornerShape(MomentumRadii.card),
                GlassLevel.HERO,
            )
            .padding(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.row,
            ),
    ) {
        MomentumButton(
            text = text,
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
