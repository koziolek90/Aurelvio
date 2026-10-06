plugins {
    `kotlin-dsl`
}

group = "pl.kozaps.aurelvio.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.kotlinx.serialization.gradlePlugin)
}
