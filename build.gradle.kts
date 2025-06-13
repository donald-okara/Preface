// Top-level build file where you can add configuration options common to all sub-projects/modules.
import dev.iurysouza.modulegraph.Theme

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    id("dev.iurysouza.modulegraph") version "0.12.0"
}

moduleGraphConfig {
    readmePath.set("${rootDir}/README.md") // or custom path
    heading.set("### Module Graph")        // heading in README
    showFullPath.set(false)                // flat layout
    nestingEnabled.set(true)               // group by path
    setStyleByModuleType.set(true)         // auto color modules
    theme.set(
        Theme.BASE(
            mapOf(
                "primaryTextColor" to "#ffffff",
                "primaryColor" to "#008080", // teal
                "primaryBorderColor" to "#006666", // darker teal
                "lineColor" to "#00b3b3", // light teal
                "tertiaryColor" to "#004c4c", // deeper background accent
                "fontSize" to "12px",
            ),
            focusColor = "#00cc99" // bright teal for highlights
        )
    )
}
