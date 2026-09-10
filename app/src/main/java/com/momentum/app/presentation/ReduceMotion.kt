package com.momentum.app.presentation

import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** True when the user disabled animations system-wide or forced it in Settings. */
@Composable
fun rememberReduceMotion(override: Boolean?): Boolean {
    val context = LocalContext.current
    if (override != null) return override
    return isSystemReduceMotion(context)
}

fun isSystemReduceMotion(context: Context): Boolean = try {
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE, 1f,
    ) == 0f
} catch (t: Throwable) {
    false
}
