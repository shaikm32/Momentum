package com.momentum.app.presentation.screens.creation

import androidx.annotation.StringRes
import com.momentum.app.R
import com.momentum.app.domain.model.HabitIntent
import com.momentum.app.domain.model.ReduceMode

/**
 * Data-driven habit templates for the unified creation screen. Selecting a
 * template populates sensible name/icon/configuration defaults; the user can
 * still edit everything afterwards. Custom stays fully free-form.
 *
 * Icon keys must exist in `MomentumIconOptions` so a template icon stays
 * re-selectable in the icon picker.
 */
data class HabitTemplate(
    val id: String,
    val intent: HabitIntent,
    /**
     * Display label in the template grid, as a resource id. Shorter than
     * [defaultTitle] because the locked 2×3 grid gives each tile one line of
     * copy; resolved with `stringResource(template.labelRes)` at the only
     * call site (`templateDisplayLabel`).
     */
    @get:StringRes val labelRes: Int,
    /**
     * Default habit name applied on selection. Plain data, not UI copy: it is
     * written onto the user's activity and matched back in edit mode, so it
     * deliberately stays a literal.
     */
    val defaultTitle: String,
    val iconKey: String,
    /**
     * Default measurable amount. Null = binary "just mark it done" (BUILD
     * only); non-null applies a target (BUILD) or limit (REDUCE).
     */
    val amount: String? = null,
    val unit: String = "",
    /** REDUCE templates always start as LIMIT; the user can switch to Completely. */
    val reduceMode: ReduceMode = ReduceMode.LIMIT,
    val isCustom: Boolean = false,
)

const val CUSTOM_BUILD_TEMPLATE_ID = "custom_build"
const val CUSTOM_REDUCE_TEMPLATE_ID = "custom_reduce"

val BuildTemplates: List<HabitTemplate> = listOf(
    HabitTemplate(
        id = "build_read",
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_read,
        defaultTitle = "Read books",
        iconKey = "learning",
        amount = "30",
        unit = "pages",
    ),
    HabitTemplate(
        id = "build_exercise",
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_exercise,
        defaultTitle = "Exercise",
        iconKey = "fitness",
    ),
    HabitTemplate(
        id = "build_meditate",
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_meditate,
        defaultTitle = "Meditate",
        iconKey = "mind",
        amount = "10",
        unit = "min",
    ),
    HabitTemplate(
        id = "build_water",
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_hydrate,
        defaultTitle = "Drink water",
        iconKey = "water",
        amount = "8",
        unit = "glasses",
    ),
    HabitTemplate(
        id = "build_learn",
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_learn,
        defaultTitle = "Learn something",
        iconKey = "school",
        amount = "30",
        unit = "min",
    ),
    HabitTemplate(
        id = CUSTOM_BUILD_TEMPLATE_ID,
        intent = HabitIntent.BUILD,
        labelRes = R.string.template_custom,
        defaultTitle = "",
        iconKey = "spark",
        isCustom = true,
    ),
)

val ReduceTemplates: List<HabitTemplate> = listOf(
    HabitTemplate(
        id = "reduce_social",
        intent = HabitIntent.REDUCE,
        labelRes = R.string.template_social_media,
        defaultTitle = "Social media",
        iconKey = "people",
        amount = "60",
        unit = "min",
    ),
    HabitTemplate(
        id = "reduce_sugar",
        intent = HabitIntent.REDUCE,
        labelRes = R.string.template_sugar,
        defaultTitle = "Sugar / junk food",
        iconKey = "food",
        amount = "2",
        unit = "times",
    ),
    HabitTemplate(
        id = "reduce_spending",
        intent = HabitIntent.REDUCE,
        labelRes = R.string.template_spending,
        defaultTitle = "Spending",
        iconKey = "list",
        amount = "50",
        unit = "$",
    ),
    HabitTemplate(
        id = "reduce_screentime",
        intent = HabitIntent.REDUCE,
        labelRes = R.string.template_screentime,
        defaultTitle = "Screen time",
        iconKey = "timer",
        amount = "120",
        unit = "min",
    ),
    HabitTemplate(
        id = CUSTOM_REDUCE_TEMPLATE_ID,
        intent = HabitIntent.REDUCE,
        labelRes = R.string.template_custom,
        defaultTitle = "",
        iconKey = "spark",
        amount = "30",
        unit = "",
        isCustom = true,
    ),
)

/** Templates appropriate to the selected intent, Custom always last. */
fun templatesFor(intent: HabitIntent): List<HabitTemplate> = when (intent) {
    HabitIntent.BUILD -> BuildTemplates
    HabitIntent.REDUCE -> ReduceTemplates
}

fun templateById(intent: HabitIntent, id: String?): HabitTemplate? =
    templatesFor(intent).firstOrNull { it.id == id }

/**
 * Matches a persisted habit back to its template (edit mode) by intent +
 * title + icon. Falls back to the Custom template id so the selection never
 * dangles — editing then preserves the loaded values untouched.
 */
fun matchTemplate(intent: HabitIntent, title: String, iconKey: String): String =
    templatesFor(intent).firstOrNull { !it.isCustom && it.defaultTitle == title && it.iconKey == iconKey }?.id
        ?: if (intent == HabitIntent.BUILD) CUSTOM_BUILD_TEMPLATE_ID else CUSTOM_REDUCE_TEMPLATE_ID
