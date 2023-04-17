package processor.factory

import com.google.devtools.ksp.symbol.KSClassDeclaration
import processor.ext.isAssisted

internal fun generateViewModelFactoryProvider(
    viewModel: KSClassDeclaration,
): String {
    val parameter = viewModel.primaryConstructor
        ?.parameters
        ?.filter { parameter -> parameter.annotations.any { it.isAssisted() } }
        ?: listOf()

    val file = viewModel.containingFile!!
    val viewModelName = viewModel.simpleName.asString()

    return if (parameter.isEmpty()) {
        generateNormalViewModelCodeFactory(viewModelName, file)
    } else {
        generateAssistedViewModelCodeFactory(viewModelName, file, parameter)
    }
}
