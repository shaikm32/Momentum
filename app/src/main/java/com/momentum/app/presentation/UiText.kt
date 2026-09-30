package com.momentum.app.presentation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A user-visible string a ViewModel must not resolve itself.
 *
 * ADR-44 keeps ViewModels free of Compose and resource APIs, yet almost all
 * of their user-visible copy belongs in `strings.xml` rather than in Kotlin.
 * A [UiText] is the contract between the two: the ViewModel chooses the
 * resource (and its format arguments) as pure data, and the screen turns it
 * into text. `resId`/`args` carry no behaviour, so the ViewModel stays
 * unit-testable exactly as before — only the assertion changes.
 *
 * The single shared representation for every ViewModel-produced message,
 * validation error, greeting and effect in the app.
 */
data class UiText(
    @StringRes val resId: Int,
    /** Positional `%1$s` / `%1$d` arguments for the resource. */
    val args: List<Any> = emptyList(),
)

/**
 * Resolves this text in the current composition — the resolution path for
 * anything rendered directly.
 */
@Composable
fun UiText.resolve(): String = stringResource(resId, *args.toTypedArray())

/**
 * Resolves this text from a [Context] — for one-shot effects, which are
 * collected inside `LaunchedEffect` where `stringResource` is not callable.
 * `getString` is what `stringResource` itself delegates to, so the rendered
 * wording is identical.
 */
fun UiText.resolve(context: Context): String = context.getString(resId, *args.toTypedArray())
