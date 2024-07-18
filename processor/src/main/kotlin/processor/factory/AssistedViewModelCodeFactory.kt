package processor.factory

import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSValueParameter
import processor.ext.isAssisted
import processor.factory.AssistedParameter.Companion.toArgument
import processor.factory.AssistedParameter.Companion.toArgumentWithAssisted
import processor.factory.AssistedParameter.Companion.toName

internal fun generateAssistedViewModelCodeFactory(
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
                    assistedFactory.create(${assistedParameter.toName()}) as T
            }

    """.trimIndent()
}

private data class AssistedParameter(
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
