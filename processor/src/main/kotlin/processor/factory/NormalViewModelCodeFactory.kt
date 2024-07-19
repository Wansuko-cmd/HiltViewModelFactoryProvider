package processor.factory

import com.google.devtools.ksp.symbol.KSFile

internal fun generateNormalViewModelCodeFactory(
    viewModelName: String,
    file: KSFile,
) = """
@file:Suppress("NOTHING_TO_INLINE")

package ${file.packageName.asString()}

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun ${viewModelName.replaceFirstChar { it.lowercase() }}(): $viewModelName = viewModel()

""".trimIndent()
