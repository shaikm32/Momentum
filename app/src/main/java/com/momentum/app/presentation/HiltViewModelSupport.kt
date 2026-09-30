package com.momentum.app.presentation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.di.AppEntryPoint
import com.momentum.app.domain.repository.ActivityRepository
import dagger.hilt.android.EntryPointAccessors

/**
 * `SavedStateHandle` argument keys for the parameterized ViewModels.
 *
 * `@HiltViewModel` constructors can only receive graph-provided types plus a
 * `SavedStateHandle`, so runtime route/composable arguments travel through the
 * handle (hiltViewModelWithArgs below) instead of constructor parameters.
 */
object ViewModelArgs {
    const val ACTIVITY_ID = "momentum.arg.activityId"
    const val ACTIVITY_TYPE = "momentum.arg.activityType"
    const val HABIT_INTENT = "momentum.arg.habitIntent"
}

/**
 * `hiltViewModel()` plus caller-supplied arguments.
 *
 * androidx.hilt's `hiltViewModel()` exposes no way to pass `CreationExtras`, so
 * it cannot seed the `SavedStateHandle` Hilt hands to the ViewModel. This
 * helper builds on the owner's default extras, merges [args] into
 * `DEFAULT_ARGS_KEY` (preserving anything navigation already put there) and
 * creates the ViewModel through the same Hilt factory.
 *
 * Args are only read when the ViewModel is first created for [owner], matching
 * the previous manual ViewModel-factory semantics.
 */
@Composable
inline fun <reified VM : ViewModel> hiltViewModelWithArgs(
    key: String? = null,
    crossinline args: Bundle.() -> Unit,
): VM {
    val owner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner provided via LocalViewModelStoreOwner"
    }
    val context = LocalContext.current
    // Same construction androidx.hilt's own createHiltViewModelFactory() does
    // (it is internal there): wrap the owner's default factory in Hilt's
    // factory, rooted at the hosting activity.
    val factory = remember(owner, context) {
        val defaultOwner = owner as HasDefaultViewModelProviderFactory
        HiltViewModelFactory(context, defaultOwner.defaultViewModelProviderFactory)
    }
    val extras = remember(owner) {
        val defaults = (owner as HasDefaultViewModelProviderFactory).defaultViewModelCreationExtras
        val merged = defaults[DEFAULT_ARGS_KEY]?.let { Bundle(it) } ?: Bundle()
        merged.apply(args)
        MutableCreationExtras(defaults).apply { set(DEFAULT_ARGS_KEY, merged) }
    }
    return viewModel<VM>(owner, key, factory, extras)
}

/**
 * The repository read used by the two non-ViewModel call sites that
 * previously used the manual container lookup.
 */
@Composable
fun rememberActivityRepository(): ActivityRepository {
    val context = LocalContext.current
    return remember(context) {
        EntryPointAccessors
            .fromApplication(context.applicationContext, AppEntryPoint::class.java)
            .activityRepository()
    }
}
