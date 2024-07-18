plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(project(":annotation"))
    implementation(libs.ksp.symbol.processing)
}
