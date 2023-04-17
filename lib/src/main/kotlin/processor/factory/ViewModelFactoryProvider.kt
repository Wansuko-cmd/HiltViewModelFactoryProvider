package processor.factory

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
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

internal data class AssistedParameter(
    val value: String,
    val name: String,
    val type: String,
) {
    companion object {
        fun create(parameter: KSValueParameter) = AssistedParameter(
            value = parameter
                .annotations
                .first { it.isAssisted() }
                .arguments[0].value.toString(),
            name = parameter.name!!.getShortName(),
            type = parameter.type.toString(),
        )

        fun List<AssistedParameter>.toArgument() = this
            .joinToString(", ") { "${it.name}: ${it.type}" }

        fun List<AssistedParameter>.toArgumentWithAssisted() = this
            .joinToString(", ") { "@Assisted(\"${it.value}\") ${it.name}: ${it.type}" }

        fun List<AssistedParameter>.toName() = this
            .joinToString(", ") { it.name }
    }
}

