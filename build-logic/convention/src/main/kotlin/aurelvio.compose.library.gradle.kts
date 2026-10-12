import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

plugins {
    id("aurelvio.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

val composeExtension = extensions.getByType<ComposeExtension>()
composeExtension.extensions.configure<org.jetbrains.compose.resources.ResourcesExtension>("resources") {
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-foundation").get())
            implementation(libs.findLibrary("compose-material3").get())
            implementation(libs.findLibrary("compose-ui").get())
            implementation(libs.findLibrary("compose-components-resources").get())
            implementation(libs.findLibrary("compose-ui-tooling-preview").get())
        }
        androidMain.dependencies {
            implementation(libs.findLibrary("compose-ui-tooling").get())
        }
    }
}

dependencies {
    add("androidRuntimeClasspath", libs.findLibrary("compose-ui-tooling").get())
}
