package processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import processor.factory.generateViewModelFactoryProvider
import processor.get.getTargetFile

class HVMFactoryProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val (valid, invalid) = getTargetFile(resolver, options)
        logger.info("HVMFactoryProvider: Get Target File")

        valid.forEach { viewModel ->
            val code = generateViewModelFactoryProvider(viewModel as KSClassDeclaration)

            codeGenerator.createNewFile(
                Dependencies(false, viewModel.containingFile!!),
                viewModel.containingFile!!.packageName.asString(),
                "${viewModel.simpleName.asString()}FactoryProvider",
            ).write(code.toByteArray())
            logger.info("HVMFactoryProvider: Generate ${viewModel.simpleName.asString()} Factory")
        }

        return invalid
    }
}
