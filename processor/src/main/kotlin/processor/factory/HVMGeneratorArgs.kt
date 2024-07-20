package processor.factory

import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isProtected
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import processor.ext.isAssisted

class HVMGeneratorArgs(
    val packageName: String,
    val viewModelName: String,
    val accessScope: String,
    val parameters: List<VMParameter>,
) {
    constructor(viewModel: KSClassDeclaration) : this(
        packageName = viewModel.containingFile!!.packageName.asString(),
        viewModelName = viewModel.simpleName.asString(),
        accessScope =
            when {
                viewModel.isPublic() -> "public"
                viewModel.isInternal() -> "internal"
                viewModel.isProtected() -> "protected"
                viewModel.isPrivate() -> "private"
                else -> "public"
            },
        parameters =
            viewModel.primaryConstructor
                ?.parameters
                ?.map { parameter -> VMParameter.create(parameter) }
                ?: emptyList(),
    )
}

sealed interface VMParameter {
    // $name: $type = $value
    val name: String
    val type: String
    val value: String

    data class Assisted(
        override val name: String,
        override val type: String,
        override val value: String,
    ) : VMParameter

    data class Normal(
        override val name: String,
        override val type: String,
        override val value: String,
    ) : VMParameter

    companion object {
        fun create(parameter: KSValueParameter): VMParameter {
            val name = parameter.name!!.getShortName()
            val type = parameter.type.toString()
            val value =
                parameter
                    .annotations
                    .first { it.isAssisted() }
                    .arguments[0]
                    .value
                    .toString()
            return if (parameter.annotations.any { it.isAssisted() }) {
                Assisted(name, type, value)
            } else {
                Normal(name, type, value)
            }
        }
    }
}
