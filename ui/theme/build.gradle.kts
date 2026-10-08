import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
	alias(libs.plugins.sle.library)
	alias(libs.plugins.sle.compose)
}

kotlin {
	explicitApi = ExplicitApiMode.Disabled
}

android {
	namespace = "org.elnix.sle.ui.theme"
}

dependencies {
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.unit)
	implementation(libs.androidx.compose.foundation)
	implementation(libs.androidx.ui)
	implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
	implementation(libs.settings.core)
	implementation(libs.settings.runtime)

	runtimeOnly(libs.androidx.lifecycle.process)

	api(libs.androidx.compose.ui.text)
	api(libs.androidx.compose.runtime)
	api(libs.androidx.ui.graphics)

	implementation(project(":ui:base"))

	api(project(":core:base"))
	implementation(project(":core:settings"))
}
