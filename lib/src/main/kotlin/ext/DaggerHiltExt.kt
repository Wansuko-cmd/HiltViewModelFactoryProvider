package ext

import com.google.devtools.ksp.symbol.KSAnnotation

fun KSAnnotation.isAssisted() =
    annotationType.resolve().declaration.packageName.asString() == "dagger.assisted" &&
        shortName.asString() == "Assisted"
