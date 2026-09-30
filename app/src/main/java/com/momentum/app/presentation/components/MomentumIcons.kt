package com.momentum.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.momentum.app.R

/**
 * Curated icon set keyed by [iconKey]. Keys are stored on activities/categories;
 * unknown keys fall back to [Star] so a bad key can never break the UI.
 * One family (Material Symbols Rounded) at consistent optical scale.
 *
 * [labelRes] carries the picker label as a resource id, never a literal: only
 * composables resolve it, via `stringResource(option.labelRes)`.
 */
data class MomentumIconOption(val key: String, @param:StringRes val labelRes: Int)

val MomentumIconOptions = listOf(
    MomentumIconOption("spark", R.string.icon_spark),
    MomentumIconOption("health", R.string.icon_health),
    MomentumIconOption("meds", R.string.icon_meds),
    MomentumIconOption("fitness", R.string.icon_fitness),
    MomentumIconOption("mind", R.string.icon_mind),
    MomentumIconOption("learning", R.string.icon_learning),
    MomentumIconOption("school", R.string.icon_school),
    MomentumIconOption("work", R.string.icon_work),
    MomentumIconOption("home", R.string.icon_home),
    MomentumIconOption("food", R.string.icon_food),
    MomentumIconOption("water", R.string.icon_water),
    MomentumIconOption("sleep", R.string.icon_sleep),
    MomentumIconOption("music", R.string.icon_music),
    MomentumIconOption("art", R.string.icon_art),
    MomentumIconOption("mail", R.string.icon_mail),
    MomentumIconOption("people", R.string.icon_people),
    MomentumIconOption("timer", R.string.icon_timer),
    MomentumIconOption("list", R.string.icon_list),
)

@Composable
fun momentumIcon(key: String): ImageVector = when (key) {
    "health" -> Icons.Rounded.Favorite
    "meds" -> Icons.Rounded.Medication
    "fitness" -> Icons.AutoMirrored.Rounded.DirectionsRun
    "mind" -> Icons.Rounded.Spa
    "learning" -> Icons.AutoMirrored.Rounded.LibraryBooks
    "school" -> Icons.Rounded.School
    "work" -> Icons.Rounded.Work
    "home" -> Icons.Rounded.Home
    "food" -> Icons.Rounded.Restaurant
    "water" -> Icons.Rounded.WaterDrop
    "sleep" -> Icons.Rounded.Bedtime
    "music" -> Icons.Rounded.MusicNote
    "art" -> Icons.Rounded.Brush
    "mail" -> Icons.Rounded.Email
    "people" -> Icons.Rounded.Person
    "timer" -> Icons.Rounded.Timer
    "list" -> Icons.AutoMirrored.Rounded.List
    "date" -> Icons.Rounded.DateRange
    "streak" -> Icons.Rounded.LocalFireDepartment
    else -> Icons.Rounded.Star
}

/**
 * The single Momentum icon set (§8): **Material Symbols Rounded** only —
 * screens reference these slots instead of importing icon families
 * directly, so no second icon library can creep in. Icon-only interactive
 * elements must meet [com.momentum.app.presentation.theme.MomentumHeights.iconTarget]
 * and pass a semantic `contentDescription` (or `null` when decorative).
 *
 * Navigation fill convention (§8): inactive tabs render the Outlined
 * style, active tabs the filled Rounded style — same family, fill varies.
 */
object MomentumIcons {
    val home = Icons.Rounded.Home
    val list = Icons.AutoMirrored.Rounded.List
    val insights = Icons.Rounded.BarChart
    val calendar = Icons.Rounded.CalendarMonth
    val settings = Icons.Rounded.Settings

    val add = Icons.Rounded.Add
    val remove = Icons.Rounded.Remove
    val search = Icons.Rounded.Search
    val check = Icons.Rounded.Check
    val edit = Icons.Rounded.Edit
    val delete = Icons.Rounded.Delete
    val archive = Icons.Rounded.Archive
    val play = Icons.Rounded.PlayArrow
    val pause = Icons.Rounded.Pause

    val arrowBack = Icons.AutoMirrored.Rounded.ArrowBack
    val arrowForward = Icons.AutoMirrored.Rounded.ArrowForward
    val chevronLeft = Icons.Rounded.ChevronLeft
    val chevronRight = Icons.Rounded.ChevronRight

    val star = Icons.Rounded.Star
    val trendingUp = Icons.AutoMirrored.Rounded.TrendingUp
    val trendingDown = Icons.AutoMirrored.Rounded.TrendingDown
}
