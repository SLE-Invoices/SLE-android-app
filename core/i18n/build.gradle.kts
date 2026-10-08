plugins {
	alias(libs.plugins.sle.library)
	alias(libs.plugins.sle.hilt)
}

android {
	namespace = "org.elnix.sle.i18n"
}

dependencies {
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.core)
	implementation(libs.dragon.logging)
	implementation(libs.commons.text) {
		exclude(group = "javax.script")
	}
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
}
