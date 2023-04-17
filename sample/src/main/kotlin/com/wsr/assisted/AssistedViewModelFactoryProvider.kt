package com.wsr.assisted

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@Composable
fun assistedViewModel(text: String): AssistedViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MemoShowViewModelFactoryProvider::class.java,
    ).assistedViewModelFactory()
    return viewModel(factory = provideFactory(factory, text))
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface MemoShowViewModelFactoryProvider {
    fun assistedViewModelFactory(): AssistedViewModelAssistedFactory
}

@AssistedFactory
interface AssistedViewModelAssistedFactory {
    fun create(
        @Assisted("text") text: String,
    ): AssistedViewModel
}

@Suppress("UNCHECKED_CAST")
private fun provideFactory(
    assistedFactory: AssistedViewModelAssistedFactory,
    text: String,
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        assistedFactory.create(text) as T
}