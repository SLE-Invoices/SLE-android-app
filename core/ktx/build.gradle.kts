plugins {
	alias(libs.plugins.sle.library)
	alias(libs.plugins.sle.compose)
}

android {
	namespace = "org.elnix.sle.ktx"
}

dependencies {
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlinx.serialization.json)

	implementation(libs.androidx.compose.runtime.annotation)
	implementation(libs.androidx.ui)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.dragon.logging)

	implementation(libs.core)
	implementation(libs.androidx.annotation)
	implementation(libs.androidx.compose.ui.util)
	implementation(libs.timber)

	api(libs.androidx.fragment)
	api(libs.androidx.ui.graphics)
	api(libs.androidx.compose.runtime)
	api(libs.androidx.compose.ui.unit)

	runtimeOnly(libs.kotlinx.coroutines.android)

	implementation(project(":core:i18n"))
}
