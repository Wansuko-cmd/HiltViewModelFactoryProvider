package processor

import annotation.HVMGenerator
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import processor.factory.generateViewModelFactoryProvider

class HVMFactoryProviderProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val assisted = resolver.getSymbolsWithAnnotation(HVMGenerator::class.java.name)
        val (valid, invalid) = assisted
            .filterIsInstance<KSClassDeclaration>()
            .partition { it.validate() }

        valid.forEach { viewModel ->
            val code = generateViewModelFactoryProvider(viewModel)
            codeGenerator.createNewFile(
                Dependencies(false, viewModel.containingFile!!),
                viewModel.containingFile!!.packageName.asString(),
                "${viewModel.simpleName.asString()}FactoryProvider",
            ).write(code.toByteArray())
        }
        return invalid
    }
}
