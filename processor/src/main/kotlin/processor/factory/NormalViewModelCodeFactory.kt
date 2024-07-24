package processor.factory

internal fun generateNormalViewModelCodeFactory(args: HVMGeneratorArgs): String =
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
    import dagger.hilt.EntryPoint
    import dagger.hilt.InstallIn
    import dagger.hilt.android.EntryPointAccessors
    import dagger.hilt.android.components.ActivityComponent
    import javax.inject.Inject
    """.trimIndent().also {
        appendLine(it)
    }

private fun StringBuilder.appendHVMGeneratorFunction(args: HVMGeneratorArgs) {
    val viewModelName = args.viewModelName
    val functionName = viewModelName.replaceFirstChar { it.lowercase() }
    """
    @Composable
    ${args.accessScope} inline fun $functionName(): $viewModelName {
        val viewModelFactory = EntryPointAccessors.fromActivity(
            activity = LocalContext.current as Activity,
            entryPoint = ${viewModelName}FactoryProvider::class.java,
        ).${functionName}Factory()
        return viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    viewModelFactory.create() as T
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
        fun ${functionName}Factory(): ${args.viewModelName}Factory
    }
    """.trimIndent().also {
        appendLine(it)
    }
}

private fun StringBuilder.appendFactory(args: HVMGeneratorArgs) {
    val normalParameters = args.parameters.filterIsInstance<VMParameter.Normal>()
    val constructorArguments =
        normalParameters.joinToString(", ") {
            "private val ${it.name} : ${it.type}"
        }
    val functionArguments = normalParameters.joinToString(", ") { it.name }
    """
    ${args.accessScope} class ${args.viewModelName}Factory @Inject constructor($constructorArguments) {
        fun create(): ${args.viewModelName} = ${args.viewModelName}($functionArguments)
    }
    """.trimIndent().also {
        appendLine(it)
    }
}
