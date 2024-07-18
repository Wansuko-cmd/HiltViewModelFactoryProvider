plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(project(":annotation"))
    implementation(libs.ksp.symbol.processing)
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "processor"
            version = libs.versions.lib.version.get()
            from(components["kotlin"])
        }
    }
}