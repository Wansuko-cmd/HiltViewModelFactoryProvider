package processor.get

import annotation.AnnotateViewModelFactory
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

fun getTargetFile(
    resolver: Resolver,
    options: Map<String, String>,
): Pair<List<KSAnnotated>, List<KSAnnotated>> = when (options["assign"]) {
    "all" -> getAllViewModel(resolver)
    "annotated" -> getAnnotatedViewModel(resolver)
    null -> getAnnotatedViewModel(resolver)
    else -> throw IllegalArgumentException("HVMFactoryProvider: Option [assign] value(${options["assign"]}) is invalid.")
}

@Suppress("UNCHECKED_CAST")
fun getAnnotatedViewModel(resolver: Resolver): Pair<List<KSClassDeclaration>, List<KSAnnotated>> {
    val assisted = resolver.getSymbolsWithAnnotation(AnnotateViewModelFactory::class.java.name)
    return assisted
        .filter { it is KSClassDeclaration }
        .partition { it.validate() && (it as KSClassDeclaration).isChildOfViewModel() }
        as Pair<List<KSClassDeclaration>, List<KSAnnotated>>
}

@Suppress("UNCHECKED_CAST")
fun getAllViewModel(resolver: Resolver): Pair<List<KSClassDeclaration>, List<KSAnnotated>> =
    resolver.getNewFiles().flatMap { file ->
        (file.declarations.filter { it is KSClassDeclaration }.toList() as List<KSClassDeclaration>)
            .filter { clazz -> clazz.isChildOfViewModel() }
    }.toList() to emptyList()

private fun KSClassDeclaration.isChildOfViewModel() =
    superTypes.any {
        val declaration = it.resolve().declaration
        declaration.packageName.asString() == "androidx.lifecycle" &&
            declaration.simpleName.getShortName() == "ViewModel"
    }
