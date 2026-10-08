plugins {
	alias(libs.plugins.sle.library)
	alias(libs.plugins.sle.serialization)
	alias(libs.plugins.sle.hilt)
}

android {
	namespace = "org.elnix.sle.network"
}

dependencies {
	api(libs.dagger)
	api(libs.kotlinx.serialization.core)
	api(libs.okhttp)

	implementation(libs.dragon.logging)
	implementation(libs.hilt.android)
	implementation(libs.hilt.core)
	implementation(libs.javax.inject)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.settings.core)
	implementation(libs.timber)

	implementation(project(":core:base"))
	implementation(project(":core:i18n"))
	implementation(project(":core:settings"))

	ksp(libs.hilt.compiler)

	runtimeOnly(libs.kotlinx.coroutines.android)
}
