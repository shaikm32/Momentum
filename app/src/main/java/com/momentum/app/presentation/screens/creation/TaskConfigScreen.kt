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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.momentum.app.R
import com.momentum.app.domain.model.ActivityType
import com.momentum.app.domain.model.Category
import com.momentum.app.domain.model.DarkModeSetting
import com.momentum.app.presentation.components.CategoryChip
import com.momentum.app.presentation.components.MomentumCard
import com.momentum.app.presentation.components.MomentumSectionHeader
import com.momentum.app.presentation.components.MomentumTopBar
import com.momentum.app.presentation.ViewModelArgs
import com.momentum.app.presentation.hiltViewModelWithArgs
import com.momentum.app.presentation.rememberReduceMotion
import com.momentum.app.presentation.resolve
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
    val vm: CreationViewModel = hiltViewModelWithArgs {
        putString(ViewModelArgs.ACTIVITY_ID, activityId?.toString())
        putString(ViewModelArgs.ACTIVITY_TYPE, ActivityType.TASK.name)
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
    TaskConfigScreen(
        form = form,
        categories = categories,
        onUpdate = { vm.onEvent(CreationEvent.Update(it)) },
        onSave = { vm.onEvent(CreationEvent.Save) },
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
            title = if (form.isNew) stringResource(R.string.task_title_new)
            else stringResource(R.string.task_title_edit),
            subtitle = stringResource(R.string.task_subtitle),
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
                    supporting = stringResource(R.string.reminder_supporting_task),
                )
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
                form.isNew -> stringResource(R.string.task_config_create)
                else -> stringResource(R.string.action_save_changes)
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
    MomentumSectionHeader(title = stringResource(R.string.due_section))
    MomentumCard {
        val today = LocalDate.now()
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
            verticalArrangement = Arrangement.spacedBy(MomentumSpacing.group),
        ) {
            DueOption.entries.forEach { option ->
                CategoryChip(
                    label = when (option) {
                        DueOption.TODAY -> stringResource(R.string.action_today)
                        DueOption.TOMORROW -> stringResource(R.string.due_tomorrow)
                        DueOption.PICK_DATE -> stringResource(R.string.due_pick_date)
                        DueOption.NO_DATE -> stringResource(R.string.due_no_date)
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
                label = stringResource(R.string.due_date_label),
                date = form.dueDate,
                onPick = { d -> onUpdate { it.copy(dueDate = d) } },
            )
        }
        if (form.dueOption == DueOption.NO_DATE) {
            Explainer(stringResource(R.string.due_no_date_explainer))
        }
        LabeledSwitch(
            label = stringResource(R.string.due_specific_time),
            supporting = stringResource(R.string.due_time_supporting),
            checked = form.hasDueTime,
            onChange = { checked -> onUpdate { it.copy(hasDueTime = checked) } },
        )
        Expandable(visible = form.hasDueTime, reduceMotion = reduceMotion) {
            TimeRow(
                label = stringResource(R.string.due_at_label),
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
    MomentumSectionHeader(title = stringResource(R.string.repeat_section))
    MomentumCard {
        LabeledSwitch(
            label = stringResource(R.string.repeat_label),
            supporting = if (form.repeatEnabled) {
                stringResource(R.string.repeat_supporting_on)
            } else {
                stringResource(R.string.repeat_supporting_off)
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
                Explainer(stringResource(R.string.repeat_explainer))
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
