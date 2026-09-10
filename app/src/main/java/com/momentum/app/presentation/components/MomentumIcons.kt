package com.momentum.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Curated icon set keyed by [iconKey]. Keys are stored on activities/categories;
 * unknown keys fall back to [Star] so a bad key can never break the UI.
 * (Extended Material icons arrive once verified in Android Studio; core set keeps
 * Phase 1 compiling on the pinned BOM with zero risk.)
 */
data class MomentumIconOption(val key: String, val label: String)

val MomentumIconOptions = listOf(
    MomentumIconOption("spark", "Spark"),
    MomentumIconOption("health", "Health"),
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
    "fitness" -> Icons.Filled.ThumbUp
    "mind" -> Icons.Filled.Face
    "learning" -> Icons.Filled.School
    "work" -> Icons.Filled.Work
    "home" -> Icons.Filled.Home
    "food" -> Icons.Filled.ShoppingCart
    "water" -> Icons.Filled.Email // placeholder glyph; refined with extended set in Studio
    "sleep" -> Icons.Filled.Timer
    "music" -> Icons.Filled.MusicNote
    "art" -> Icons.Filled.Brush
    "mail" -> Icons.Filled.Email
    "people" -> Icons.Filled.Person
    "timer" -> Icons.Filled.Timer
    "list" -> Icons.Filled.List
    "folder" -> Icons.Filled.List
    "date" -> Icons.Filled.DateRange
    else -> Icons.Filled.Star
}
