package processor.ext

import com.google.devtools.ksp.symbol.KSAnnotation

internal fun KSAnnotation.isAssisted() =
    annotationType.resolve().declaration.packageName.asString() == "dagger.assisted" &&
        shortName.asString() == "Assisted"
