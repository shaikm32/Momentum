package com.momentum.app.presentation.screens.activityedit

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.Category
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.MomentumIconOptions
import com.momentum.app.presentation.components.momentumIcon
import com.momentum.app.presentation.momentumViewModel
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
    val c = MomentumThemeColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    if (form.isNew) "New activity" else "Edit activity",
                    style = MaterialTheme.typography.headlineMedium,
                    color = c.textPrimary,
                )
            }
        }
        item {
            OutlinedTextField(
                value = form.title,
                onValueChange = { v -> onUpdate { it.copy(title = v) } },
                label = { Text("What do you want to accomplish?") },
                singleLine = true,
                isError = form.titleError != null,
                supportingText = { form.titleError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SectionTitle("Icon")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MomentumIconOptions.forEach { option ->
                    val selected = form.iconKey == option.key
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (selected) c.primary else c.surface.copy(alpha = 0.72f))
                            .clickable { onUpdate { it.copy(iconKey = option.key) } },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            momentumIcon(option.key),
                            contentDescription = option.label,
                            tint = if (selected) c.onPrimary else c.textSecondary,
                        )
                    }
                }
            }
        }
        item {
            SectionTitle("Category")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    CategoryChip(
                        label = cat.name,
                        selected = form.categoryId == cat.id,
                        onClick = { onUpdate { it.copy(categoryId = if (it.categoryId == cat.id) null else cat.id) } },
                    )
                }
            }
        }
        item {
            SectionTitle("Schedule")
            RecurrenceChoiceRow(form = form, onUpdate = onUpdate)
            Spacer(Modifier.height(8.dp))
            when (form.recurrenceChoice) {
                RecurrenceChoice.CUSTOM_DAYS, RecurrenceChoice.EVERY_N_WEEKS ->
                    WeekdayPicker(selected = form.days, onToggle = { day ->
                        onUpdate { it.copy(days = if (day in it.days) it.days - day else it.days + day) }
                    })
                RecurrenceChoice.EVERY_N_DAYS, RecurrenceChoice.EVERY_N_WEEKS -> {}
                else -> {}
            }
            if (form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS ||
                form.recurrenceChoice == RecurrenceChoice.EVERY_N_WEEKS
            ) {
                Stepper(
                    label = "Every ${form.interval} ${if (form.recurrenceChoice == RecurrenceChoice.EVERY_N_DAYS) "days" else "weeks"}",
                    value = form.interval,
                    range = 1..30,
                    onChange = { v -> onUpdate { it.copy(interval = v) } },
                )
            }
            if (form.recurrenceChoice == RecurrenceChoice.MONTHLY) {
                Stepper(
                    label = "Day ${form.dayOfMonth} of the month",
                    value = form.dayOfMonth,
                    range = 1..31,
                    onChange = { v -> onUpdate { it.copy(dayOfMonth = v) } },
                )
            }
        }
        item {
            SectionTitle("Dates")
            DateRow(label = "Starts", date = form.startDate, onPick = { d -> onUpdate { it.copy(startDate = d) } })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Has end date", color = c.textPrimary, modifier = Modifier.weight(1f))
                Switch(
                    checked = form.hasEndDate,
                    onCheckedChange = { checked -> onUpdate { it.copy(hasEndDate = checked) } },
                )
            }
            if (form.hasEndDate) {
                DateRow(label = "Ends", date = form.endDate, onPick = { d -> onUpdate { it.copy(endDate = d) } })
            }
        }
        item {
            SectionTitle("Target (optional)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = form.target,
                    onValueChange = { v -> onUpdate { it.copy(target = v) } },
                    label = { Text("Amount") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = form.unit,
                    onValueChange = { v -> onUpdate { it.copy(unit = v) } },
                    label = { Text("Unit (glasses, min…)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            SectionTitle("Priority")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (0..3).forEach { p ->
                    CategoryChip(
                        label = when (p) {
                            0 -> "Normal"
                            1 -> "Medium"
                            2 -> "High"
                            else -> "Top"
                        },
                        selected = form.priority == p,
                        onClick = { onUpdate { it.copy(priority = p) } },
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = form.notes,
                onValueChange = { v -> onUpdate { it.copy(notes = v) } },
                label = { Text("Notes (dosage, instructions…)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reminder", color = c.textPrimary, modifier = Modifier.weight(1f))
                Switch(
                    checked = form.reminderEnabled,
                    onCheckedChange = { checked -> onUpdate { it.copy(reminderEnabled = checked) } },
                )
            }
            if (form.reminderEnabled) {
                ReminderTimeRow(time = form.reminderTime, onPick = { t -> onUpdate { it.copy(reminderTime = t) } })
            }
        }
        item {
            Button(
                onClick = onSave,
                enabled = !form.isSaving,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) {
                Text(if (form.isSaving) "Saving…" else "Save activity")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        color = MomentumThemeColors.current.textPrimary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceChoiceRow(
    form: EditForm,
    onUpdate: ((EditForm) -> EditForm) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, color = MomentumThemeColors.current.textPrimary, modifier = Modifier.weight(1f))
        IconButton(onClick = { onChange((value - 1).coerceIn(range)) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease")
        }
        Text("$value", style = MaterialTheme.typography.titleLarge, color = MomentumThemeColors.current.textPrimary)
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
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface.copy(alpha = 0.72f))
            .clickable { open = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = c.textSecondary, modifier = Modifier.weight(1f))
        Text(
            date.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy")),
            color = c.textPrimary,
            style = MaterialTheme.typography.titleLarge,
        )
    }
    if (open) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
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
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface.copy(alpha = 0.72f))
            .clickable { open = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Time", color = c.textSecondary, modifier = Modifier.weight(1f))
        Text(time, color = c.textPrimary, style = MaterialTheme.typography.titleLarge)
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
