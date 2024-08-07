plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

publishing {
    publications {
        create<MavenPublication>("annotation") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "annotation"
            version = libs.versions.lib.version.get()
            from(components["kotlin"])
        }
    }
}
