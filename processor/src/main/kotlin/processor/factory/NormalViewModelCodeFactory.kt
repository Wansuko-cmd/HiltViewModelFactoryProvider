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
    }


private fun StringBuilder.appendImports() =
    """
    import androidx.compose.runtime.Composable
    import androidx.hilt.navigation.compose.hiltViewModel
    """.trimIndent().also {
        appendLine(it)
    }

private fun StringBuilder.appendHVMGeneratorFunction(args: HVMGeneratorArgs) {
    val viewModelName = args.viewModelName
    val functionName = viewModelName.replaceFirstChar { it.lowercase() }
    """
    @Composable
    ${args.accessScope} inline fun $functionName(): $viewModelName = hiltViewModel()
    """.trimIndent().also {
        appendLine(it)
    }
}
