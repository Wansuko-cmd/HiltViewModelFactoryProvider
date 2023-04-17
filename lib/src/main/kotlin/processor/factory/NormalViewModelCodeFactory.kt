package processor.factory

import com.google.devtools.ksp.symbol.KSFile

internal fun generateNormalViewModelCodeFactory(
    viewModelName: String,
    file: KSFile,
) = """
        package ${file.packageName.asString()}
                
        import androidx.compose.runtime.Composable
        import androidx.hilt.navigation.compose.hiltViewModel

        @Composable
        fun ${viewModelName.replaceFirstChar { it.lowercase() }}(): $viewModelName = hiltViewModel()
""".trimIndent()
