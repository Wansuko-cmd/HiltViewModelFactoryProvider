package processor.factory

import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isProtected
import com.google.devtools.ksp.isPublic
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
    val accessScope = when {
        viewModel.isPublic() -> "public"
        viewModel.isInternal() -> "internal"
        viewModel.isProtected() -> "protected"
        viewModel.isPrivate() -> "private"
        else -> "public"
    }

    return if (parameter.isEmpty()) {
        generateNormalViewModelCodeFactory(viewModelName, file, accessScope)
    } else {
        generateAssistedViewModelCodeFactory(HVMGeneratorArgs(viewModel))
    }
}
