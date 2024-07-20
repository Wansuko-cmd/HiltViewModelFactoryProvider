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
    accessScope: String,
): String {
    val assistedParameter = parameter.map { AssistedParameter.create(it) }

    return """
@file:Suppress("NOTHING_TO_INLINE")

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
$accessScope inline fun ${viewModelName.replaceFirstChar { it.lowercase() }}(${assistedParameter.toArgument()}): $viewModelName {
    val assistedViewModelFactory = EntryPointAccessors.fromActivity(
        activity = LocalContext.current as Activity,
        entryPoint = ${viewModelName}FactoryProvider::class.java,
    ).assistedViewModelFactory()
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                assistedViewModelFactory.create(${assistedParameter.toName()}) as T
        }
    )
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
            .joinToString(", ") { (value, name, type) ->
                "@Assisted${if (value.isNotBlank()) "(\"$value\")" else ""} $name: $type" }

        fun List<AssistedParameter>.toName() = this
            .joinToString(", ") { it.name }
    }
}
