package com.momentum.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentum.app.AppContainer
import com.momentum.app.MomentumApp

/** Reads the manual DI container (ADR-09) from the application. */
@Composable
fun rememberContainer(): AppContainer {
    val context = LocalContext.current
    return (context.applicationContext as MomentumApp).container
}

@Suppress("UNCHECKED_CAST")
private class MomentumFactory<T : ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
}

/** Creates a ViewModel wired to the [AppContainer], e.g. `momentumViewModel { TodayViewModel(c, …) }`. */
@Composable
inline fun <reified T : ViewModel> momentumViewModel(noinline create: (AppContainer) -> T): T {
    val container = rememberContainer()
    return viewModel(modelClass = T::class.java, factory = MomentumFactory { create(container) })
}
