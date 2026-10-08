plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
}

group = "org.elnix.sle.buildlogic"

dependencies {
	compileOnly(libs.android.gradle.plugin)
	compileOnly(libs.android.gradle.api)
	compileOnly(libs.kotlin.gradle.plugin.api)
	compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
	plugins {
		register("sleLibrary") {
			id = "sle.library"
			implementationClass = "sle.buildlogic.SleAndroidLibraryPlugin"
		}
		register("sleCompose") {
			id = "sle.compose"
			implementationClass = "sle.buildlogic.SleAndroidComposePlugin"
		}
		register("sleSerialization") {
			id = "sle.serialization"
			implementationClass = "sle.buildlogic.SleAndroidSerializationPlugin"
		}
		register("sleHilt") {
			id = "sle.hilt"
			implementationClass = "sle.buildlogic.SleHiltPlugin"
		}
		register("sleApplication") {
			id = "sle.application"
			implementationClass = "sle.buildlogic.SleAndroidApplicationPlugin"
		}
	}
}
