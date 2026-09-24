package com.momentum.app.presentation.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.momentumViewModel
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.theme.MomentumSpacing
import com.momentum.app.presentation.theme.MomentumTheme
import java.time.LocalDate

/**
 * Task configuration: what it is, when it's due (date + optional time),
 * whether it repeats, and an optional reminder kept separate from the due
 * time. No habit concepts (intent, streaks, targets) appear here, and the
 * saved row stays [ActivityType.TASK].
 */
@Composable
fun TaskConfigRoute(
    activityId: Long?,
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val vm: CreationViewModel = momentumViewModel {
        CreationViewModel(it, activityId, ActivityType.TASK)
    }
    val form by vm.form.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                is CreationEffect.Saved -> onSaved(effect.activityId)
                is CreationEffect.Error -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    TaskConfigScreen(
        form = form,
        categories = categories,
        onUpdate = { vm.update(it) },
        onSave = { vm.save() },
        onBack = onBack,
    )
}

@Composable
fun TaskConfigScreen(
    form: CreationForm,
    categories: List<Category>,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val reduceMotion = rememberReduceMotion(null)
    Column(modifier = Modifier.fillMaxSize()) {
        MomentumTopBar(
            title = if (form.isNew) "New task" else "Edit task",
            subtitle = "What do you need to get done?",
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
            item {
                NameAndIconSection(form = form, onUpdate = onUpdate)
            }
            item {
                DueSection(form = form, onUpdate = onUpdate, reduceMotion = reduceMotion)
            }
            item {
                RepeatSection(form = form, onUpdate = onUpdate, reduceMotion = reduceMotion)
            }
            item {
                ReminderSection(
                    form = form,
                    onUpdate = onUpdate,
                    reduceMotion = reduceMotion,
                    supporting = "Separate from the due time — when to nudge you",
                )
            }
            item {
                MomentumSectionHeader(title = "Details (optional)")
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
                form.isSaving -> "Saving…"
                form.isNew -> "Create task"
                else -> "Save changes"
            },
            enabled = !form.isSaving && form.isSubmittable(),
            onClick = onSave,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DueSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
) {
    MomentumSectionHeader(title = "When is it due?")
    MomentumCard {
        val today = LocalDate.now()
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            DueOption.entries.forEach { option ->
                CategoryChip(
                    label = when (option) {
                        DueOption.TODAY -> "Today"
                        DueOption.TOMORROW -> "Tomorrow"
                        DueOption.PICK_DATE -> "Pick a date"
                        DueOption.NO_DATE -> "No due date"
                    },
                    selected = form.dueOption == option,
                    onClick = {
                        onUpdate {
                            it.copy(
                                dueOption = option,
                                dueDate = when (option) {
                                    DueOption.TODAY, DueOption.NO_DATE -> today
                                    DueOption.TOMORROW -> today.plusDays(1)
                                    DueOption.PICK_DATE -> it.dueDate
                                },
                            )
                        }
                    },
                )
            }
        }
        Expandable(visible = form.dueOption == DueOption.PICK_DATE, reduceMotion = reduceMotion) {
            DateRow(
                label = "Due date",
                date = form.dueDate,
                onPick = { d -> onUpdate { it.copy(dueDate = d) } },
            )
        }
        if (form.dueOption == DueOption.NO_DATE) {
            Explainer("Shows up today until it's done.")
        }
        LabeledSwitch(
            label = "Specific time",
            supporting = "What time it's due — separate from the reminder",
            checked = form.hasDueTime,
            onChange = { checked -> onUpdate { it.copy(hasDueTime = checked) } },
        )
        Expandable(visible = form.hasDueTime, reduceMotion = reduceMotion) {
            TimeRow(
                label = "Due at",
                time = form.dueTimePicker,
                onPick = { t -> onUpdate { it.copy(dueTimePicker = t) } },
            )
        }
    }
}

@Composable
private fun RepeatSection(
    form: CreationForm,
    onUpdate: ((CreationForm) -> CreationForm) -> Unit,
    reduceMotion: Boolean,
) {
    MomentumSectionHeader(title = "Does it repeat?")
    MomentumCard {
        LabeledSwitch(
            label = "Repeat",
            supporting = if (form.repeatEnabled) {
                "Each occurrence is independent — it stays a task"
            } else {
                "One-time — monthly bills and weekly chores can repeat"
            },
            checked = form.repeatEnabled,
            onChange = { on ->
                onUpdate {
                    it.copy(
                        repeatEnabled = on,
                        recurrenceChoice = when {
                            on && it.recurrenceChoice == RecurrenceChoice.ONCE ->
                                RecurrenceChoice.DAILY
                            !on -> RecurrenceChoice.ONCE
                            else -> it.recurrenceChoice
                        },
                    )
                }
            },
        )
        Expandable(visible = form.repeatEnabled, reduceMotion = reduceMotion) {
            Column(verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group)) {
                Explainer("Repeats from the due date above.")
                ScheduleSection(
                    form = form,
                    onUpdate = onUpdate,
                    reduceMotion = reduceMotion,
                    anchorDate = form.dueDate,
                    onAnchorDate = {},
                    showAnchorDate = false,
                    showEndDate = false,
                    bare = true,
                )
            }
        }
    }
}

@Preview(name = "Task config — light", showBackground = true)
@Composable
private fun TaskConfigPreviewLight() {
    MomentumTheme(themeId = "twilight", darkMode = DarkModeSetting.LIGHT) {
        TaskConfigScreen(
            form = CreationForm(
                activityType = ActivityType.TASK,
                title = "Pay rent",
                hasDueTime = true,
            ),
            categories = emptyList(),
            onUpdate = {},
            onSave = {},
            onBack = {},
        )
    }
}

@Preview(name = "Task config repeat — dark", showBackground = true)
@Composable
private fun TaskConfigRepeatPreviewDark() {
    MomentumTheme(themeId = "twilight", darkMode = DarkModeSetting.DARK) {
        TaskConfigScreen(
            form = CreationForm(
                activityType = ActivityType.TASK,
                title = "Water the plants",
                recurrenceChoice = RecurrenceChoice.WEEKLY,
                repeatEnabled = true,
            ),
            categories = emptyList(),
            onUpdate = {},
            onSave = {},
            onBack = {},
        )
    }
}
