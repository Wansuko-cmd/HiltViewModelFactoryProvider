package processor.factory

internal fun generateAssistedViewModelCodeFactory(args: HVMGeneratorArgs): String =
    buildString {
        appendLine("@file:Suppress(\"NOTHING_TO_INLINE\")")
        appendLine()
        appendLine("package ${args.packageName}")
        appendLine()
        appendImports()
        appendLine()
        appendHVMGeneratorFunction(args = args)
        appendLine()
        appendFactoryProvider(args = args)
        appendLine()
        appendFactory(args = args)
    }

private fun StringBuilder.appendImports() =
    """
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
    """.trimIndent().also {
        appendLine(it)
    }

private fun StringBuilder.appendHVMGeneratorFunction(args: HVMGeneratorArgs) {
    val viewModelName = args.viewModelName
    val functionName = viewModelName.replaceFirstChar { it.lowercase() }
    val assistedParameters = args.parameters.filterIsInstance<VMParameter.Assisted>()
    val funcArguments = assistedParameters.joinToString(", ") { "${it.name}: ${it.type}" }
    """
    @Composable
    ${args.accessScope} inline fun $functionName($funcArguments): $viewModelName {
        val assistedViewModelFactory = EntryPointAccessors.fromActivity(
            activity = LocalContext.current as Activity,
            entryPoint = ${viewModelName}FactoryProvider::class.java,
        ).${functionName}AssistedFactory()
        return viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    assistedViewModelFactory.create(${assistedParameters.joinToString(", ") { it.name }}) as T
            }
        )
    }
    """.trimIndent().also {
        appendLine(it)
    }
}

private fun StringBuilder.appendFactoryProvider(args: HVMGeneratorArgs) {
    val functionName = args.viewModelName.replaceFirstChar { it.lowercase() }
    """
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    ${args.accessScope} interface ${args.viewModelName}FactoryProvider {
        fun ${functionName}AssistedFactory(): ${args.viewModelName}AssistedFactory
    }
    """.trimIndent().also {
        appendLine(it)
    }
}

private fun StringBuilder.appendFactory(args: HVMGeneratorArgs) {
    val arguments =
        args.parameters
            .filterIsInstance<VMParameter.Assisted>()
            .joinToString(", ") { (name, type, value) ->
                "@Assisted${if (value.isNotBlank()) "(\"$value\")" else ""} $name: $type"
            }
    """
    @AssistedFactory
    ${args.accessScope} interface ${args.viewModelName}AssistedFactory {
        fun create(
            $arguments,
        ): ${args.viewModelName}
    }
    """.trimIndent().also {
        appendLine(it)
    }
}
