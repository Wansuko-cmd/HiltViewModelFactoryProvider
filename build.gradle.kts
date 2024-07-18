plugins {
    id("maven-publish")
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

subprojects {
    apply(plugin = "maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            groupId = "io.github.bugdog24"
            artifactId = "hilt-view-model-factory-provider-processor"
            version = "0.0.1-alpha1"
        }

        create<MavenPublication>("annotation") {
            groupId = "io.github.bugdog24"
            artifactId = "hilt-view-model-factory-provider-annotation"
            version = "0.0.1-alpha1"
        }
    }
}
