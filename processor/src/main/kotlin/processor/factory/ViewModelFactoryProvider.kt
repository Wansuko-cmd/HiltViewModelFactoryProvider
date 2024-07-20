package processor.factory

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal fun generateViewModelFactoryProvider(viewModel: KSClassDeclaration): String =
    HVMGeneratorArgs(viewModel).let { args ->
        if (args.parameters.any { it is VMParameter.Assisted }) {
            generateAssistedViewModelCodeFactory(args)
        } else {
            generateNormalViewModelCodeFactory(args)
        }
    }
