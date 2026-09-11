package com.momentum.app.presentation.screens.activityedit

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.Category
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.GlassLevel
import com.momentum.app.presentation.components.MomentumButton
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.components.glassPress
import com.momentum.app.presentation.components.liquidGlass
import com.momentum.app.presentation.components.MomentumIconOptions
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumIconSizes
import com.momentum.app.presentation.theme.MomentumGlass
import com.momentum.app.presentation.theme.MomentumRadii
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumThemeColors
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActivityEditRoute(
    activityId: Long?,
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: ActivityEditViewModel = momentumViewModel { ActivityEditViewModel(it, activityId) }
    val form by vm.form.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is EditEffect.Saved -> onSaved(effect.activityId)
                is EditEffect.Error -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    ActivityEditScreen(
        form = form,
        categories = categories,
        onUpdate = { vm.update(it) },
        onSave = { vm.save() },
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityEditScreen(
    form: EditForm,
    categories: List<Category>,
    onUpdate: ((EditForm) -> EditForm) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val reduceMotion = rememberReduceMotion(null)
    Column(modifier = Modifier.fillMaxSize()) {
        MomentumTopBar(
            title = if (form.isNew) "New activity" else "Edit activity",
            subtitle = if (form.isNew) "Small steps, done daily" else null,
            onBack = onBack,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                horizontal = MomentumSpacing.screen,
                vertical = MomentumSpacing.group,
            ),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.item),
        ) {
            // ——— BASIC ———
            item {
                MomentumSectionHeader(title = "Basic")
            }
            item {
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { v -> onUpdate { it.copy(title = v) } },
                    label = { Text("What do you want to accomplish?") },
                    singleLine = true,
                    isError = form.titleError != null,
                    supportingText = { form.titleError?.let { Text(it) } },
                    shape = RoundedCornerShape(MomentumRadii.control),
                    colors = glassFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                MomentumCard {
                    Text(
                        "Icon",
                        style = MaterialTheme.typography.titleMedium,
                        color = MomentumThemeColors.current.textPrimary,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                    ) {
                        MomentumIconOptions.forEach { option ->
                            IconChoice(
                                selected = form.iconKey == option.key,
                                label = option.label,
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
            if (categories.isNotEmpty()) {
                item {
                    MomentumCard {
                        Text(
                            "Category",
                            style = MaterialTheme.typography.titleMedium,
                            color = MomentumThemeColors.current.textPrimary,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        ) {
                            CategoryChip(
                                label = "None",
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
            }
            // ——— SCHEDULE ———
            item {
                MomentumSectionHeader(title = "Schedule")
            }
            item {
                MomentumCard {
                    RecurrenceChoiceRow(form = form, onUpdate = onUpdate)
                    Expandable(visible = form.recurrenceChoice == RecurrenceChoice.CUSTOM_DAYS ||
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
                    Expandable(
                        visible = form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS ||
                            form.recurrenceChoice == RecurrenceChoice.EVERY_N_WEEKS,
                        reduceMotion = reduceMotion,
                    ) {
                        Stepper(
                            label = "Every ${form.interval} " +
                                if (form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS) "days" else "weeks",
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
                            label = "Day ${form.dayOfMonth} of the month",
                            value = form.dayOfMonth,
                            range = 1..31,
                            onChange = { v -> onUpdate { it.copy(dayOfMonth = v) } },
                        )
                    }
                    DateRow(
                        label = "Starts",
                        date = form.startDate,
                        onPick = { d -> onUpdate { it.copy(startDate = d) } },
                    )
                    LabeledSwitch(
                        label = "End date",
                        supporting = if (form.hasEndDate) "Stops repeating" else "Repeats indefinitely",
                        checked = form.hasEndDate,
                        onChange = { checked -> onUpdate { it.copy(hasEndDate = checked) } },
                    )
                    Expandable(visible = form.hasEndDate, reduceMotion = reduceMotion) {
                        DateRow(
                            label = "Ends",
                            date = form.endDate,
                            onPick = { d -> onUpdate { it.copy(endDate = d) } },
                        )
                    }
                }
            }
            // ——— GOAL ———
            item {
                MomentumSectionHeader(title = "Goal")
            }
            item {
                MomentumCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                        OutlinedTextField(
                            value = form.target,
                            onValueChange = { v -> onUpdate { it.copy(target = v) } },
                            label = { Text("Amount") },
                            singleLine = true,
                            shape = RoundedCornerShape(MomentumRadii.control),
                            colors = glassFieldColors(),
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = form.unit,
                            onValueChange = { v -> onUpdate { it.copy(unit = v) } },
                            label = { Text("Unit") },
                            placeholder = { Text("glasses, min…") },
                            singleLine = true,
                            shape = RoundedCornerShape(MomentumRadii.control),
                            colors = glassFieldColors(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            // ——— PRIORITY ———
            item {
                MomentumSectionHeader(title = "Priority")
            }
            item {
                MomentumCard {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        listOf("Normal", "Medium", "High", "Top").forEachIndexed { p, label ->
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
            // ——— NOTES ———
            item {
                MomentumSectionHeader(title = "Notes")
            }
            item {
                OutlinedTextField(
                    value = form.notes,
                    onValueChange = { v -> onUpdate { it.copy(notes = v) } },
                    label = { Text("Dosage, instructions…") },
                    shape = RoundedCornerShape(MomentumRadii.control),
                    colors = glassFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
            }
            // ——— REMINDER ———
            item {
                MomentumSectionHeader(title = "Reminder")
            }
            item {
                MomentumCard {
                    LabeledSwitch(
                        label = "Daily nudge",
                        supporting = "On-device notification at your time",
                        checked = form.reminderEnabled,
                        onChange = { checked -> onUpdate { it.copy(reminderEnabled = checked) } },
                    )
                    Expandable(visible = form.reminderEnabled, reduceMotion = reduceMotion) {
                        ReminderTimeRow(
                            time = form.reminderTime,
                            onPick = { t -> onUpdate { it.copy(reminderTime = t) } },
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(MomentumSpacing.group))
            }
        }
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
                text = if (form.isSaving) "Saving…" else "Save activity",
                onClick = onSave,
                enabled = !form.isSaving,
            )
        }
    }
}

/** Translucent text-field colors shared by every field on this screen. */
@Composable
private fun glassFieldColors(): androidx.compose.material3.TextFieldColors {
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
private fun Expandable(
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
private fun IconChoice(
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
private fun LabeledSwitch(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceChoiceRow(
    form: EditForm,
    onUpdate: ((EditForm) -> EditForm) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val c = MomentumThemeColors.current
    val labels = mapOf(
        RecurrenceChoice.ONCE to "One-time",
        RecurrenceChoice.DAILY to "Daily",
        RecurrenceChoice.WEEKDAYS to "Weekdays (Mon–Fri)",
        RecurrenceChoice.CUSTOM_DAYS to "Specific days",
        RecurrenceChoice.EVERY_N_DAYS to "Every N days",
        RecurrenceChoice.WEEKLY to "Weekly",
        RecurrenceChoice.EVERY_N_WEEKS to "Every N weeks",
        RecurrenceChoice.MONTHLY to "Monthly",
    )
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = labels[form.recurrenceChoice].orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Repeats") },
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
private fun WeekdayPicker(selected: Set<DayOfWeek>, onToggle: (DayOfWeek) -> Unit) {
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
private fun Stepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    val c = MomentumThemeColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = c.textPrimary, modifier = Modifier.weight(1f))
        IconButton(onClick = { onChange((value - 1).coerceIn(range)) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease")
        }
        Text(
            "$value",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = c.textPrimary,
        )
        IconButton(onClick = { onChange((value + 1).coerceIn(range)) }) {
            Icon(Icons.Filled.Add, contentDescription = "Increase")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRow(label: String, date: LocalDate, onPick: (LocalDate) -> Unit) {
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
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary, modifier = Modifier.weight(1f))
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
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeRow(time: String, onPick: (String) -> Unit) {
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
            .clickable(onClickLabel = "Reminder time") { open = true }
            .padding(MomentumSpacing.card),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Time", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary, modifier = Modifier.weight(1f))
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
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
            text = { TimePicker(state = state) },
        )
    }
}
