plugins {
    id("maven-publish")
}

subprojects {
    apply(plugin = "maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "hilt-view-model-factory-provider-processor"
            version = libs.versions.lib.version.get()
        }

        create<MavenPublication>("annotation") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "hilt-view-model-factory-provider-annotation"
            version = libs.versions.lib.version.get()
        }
    }
}
