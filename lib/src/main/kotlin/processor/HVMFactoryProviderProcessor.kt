package processor

import annotation.AssistedHiltViewModel
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import ext.isAssisted
import processor.HVMFactoryProviderProcessor.AssistedParameter.Companion.toArgument
import processor.HVMFactoryProviderProcessor.AssistedParameter.Companion.toArgumentWithAssisted
import processor.HVMFactoryProviderProcessor.AssistedParameter.Companion.toName

class HVMFactoryProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val assisted = resolver.getSymbolsWithAnnotation(AssistedHiltViewModel::class.java.name)
        val (valid, invalid) = assisted
            .filter { it is KSClassDeclaration }
            .partition { it.validate() }

        valid.forEach { viewModel ->
            val parameter = (viewModel as KSClassDeclaration)
                .primaryConstructor
                ?.parameters
                ?.filter { parameter ->
                    parameter.annotations
                        .any { it.isAssisted() }
                } ?: listOf()
            generateViewModelFactoryProvider(viewModel, parameter)
        }
        return invalid
    }

    private fun generateViewModelFactoryProvider(
        viewModel: KSClassDeclaration,
        parameter: List<KSValueParameter> = listOf(),
    ) {
        val file = viewModel.containingFile!!
        val viewModelName = viewModel.simpleName.asString()
        val code = if (parameter.isEmpty()) {
            generateNormalViewModelFactoryCode(viewModelName, file)
        } else {
            generateAssistedViewModelFactoryCode(viewModelName, file, parameter)
        }

        codeGenerator.createNewFile(
            Dependencies(false, file),
            file.packageName.asString(),
            "${viewModelName}FactoryProvider",
        ).write(code.toByteArray())
    }

    private fun generateNormalViewModelFactoryCode(
        viewModelName: String,
        file: KSFile,
    ) = """
        package ${file.packageName.asString()}
                
        import androidx.compose.runtime.Composable
        import androidx.hilt.navigation.compose.hiltViewModel

        @Composable
        fun ${viewModelName.replaceFirstChar { it.lowercase() }}(): $viewModelName = hiltViewModel()
    """.trimIndent()

    private fun generateAssistedViewModelFactoryCode(
        viewModelName: String,
        file: KSFile,
        parameter: List<KSValueParameter>,
    ): String {
        val assistedParameter = parameter.map { AssistedParameter.create(it) }

        return """
            package ${file.packageName.asString()}
            
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
            fun ${viewModelName.replaceFirstChar { it.lowercase() }}(${assistedParameter.toArgument()}): $viewModelName {
                val factory = EntryPointAccessors.fromActivity(
                    LocalContext.current as Activity,
                    ${viewModelName}FactoryProvider::class.java,
                ).assistedViewModelFactory()
                return viewModel(factory = provideFactory(factory, ${assistedParameter.toName()}))
            }

            @EntryPoint
            @InstallIn(ActivityComponent::class)
            interface ${viewModelName}FactoryProvider {
                fun assistedViewModelFactory(): ${viewModelName}AssistedFactory
            }

            @AssistedFactory
            interface ${viewModelName}AssistedFactory {
                fun create(
                    ${assistedParameter.toArgumentWithAssisted()},
                ): $viewModelName
            }

            @Suppress("UNCHECKED_CAST")
            private fun provideFactory(
                assistedFactory: ${viewModelName}AssistedFactory,
                ${assistedParameter.toArgument()},
            ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    assistedFactory.create(text) as T
            }
        """.trimIndent()
    }

    data class AssistedParameter(
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
}
