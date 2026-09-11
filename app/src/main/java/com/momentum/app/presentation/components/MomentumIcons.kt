package com.momentum.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Curated icon set keyed by [iconKey]. Keys are stored on activities/categories;
 * unknown keys fall back to [Star] so a bad key can never break the UI.
 * One family (Material filled) at consistent optical scale.
 */
data class MomentumIconOption(val key: String, val label: String)

val MomentumIconOptions = listOf(
    MomentumIconOption("spark", "Spark"),
    MomentumIconOption("health", "Health"),
    MomentumIconOption("meds", "Medicine"),
    MomentumIconOption("fitness", "Fitness"),
    MomentumIconOption("mind", "Mind"),
    MomentumIconOption("learning", "Learning"),
    MomentumIconOption("work", "Work"),
    MomentumIconOption("home", "Home"),
    MomentumIconOption("food", "Food"),
    MomentumIconOption("water", "Water"),
    MomentumIconOption("sleep", "Sleep"),
    MomentumIconOption("music", "Music"),
    MomentumIconOption("art", "Art"),
    MomentumIconOption("mail", "Mail"),
    MomentumIconOption("people", "People"),
    MomentumIconOption("timer", "Timer"),
    MomentumIconOption("list", "List"),
)

@Composable
fun momentumIcon(key: String): ImageVector = when (key) {
    "health" -> Icons.Filled.Favorite
    "meds" -> Icons.Filled.Medication
    "fitness" -> Icons.AutoMirrored.Filled.DirectionsRun
    "mind" -> Icons.Filled.SelfImprovement
    "learning" -> Icons.AutoMirrored.Filled.MenuBook
    "work" -> Icons.Filled.Work
    "home" -> Icons.Filled.Home
    "food" -> Icons.Filled.Restaurant
    "water" -> Icons.Filled.WaterDrop
    "sleep" -> Icons.Filled.Bedtime
    "music" -> Icons.Filled.Spa
    "art" -> Icons.Filled.Brush
    "mail" -> Icons.Filled.Email
    "people" -> Icons.Filled.Person
    "timer" -> Icons.Filled.Timer
    "list" -> Icons.AutoMirrored.Filled.List
    "school" -> Icons.Filled.School
    "folder" -> Icons.AutoMirrored.Filled.List
    "date" -> Icons.Filled.DateRange
    else -> Icons.Filled.Star
}
