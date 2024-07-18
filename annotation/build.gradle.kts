plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "hilt-view-model-factory-provider-processor"
            version = libs.versions.lib.version.get()
            from(components["kotlin"])
        }
    }
}
