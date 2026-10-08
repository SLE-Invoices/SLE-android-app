pluginManagement {
	repositories {
		google {
			content {
				includeGroupByRegex("com\\.android.*")
				includeGroupByRegex("com\\.google.*")
				includeGroupByRegex("androidx.*")
			}
		}
		mavenCentral()
		mavenLocal()
		gradlePluginPortal()
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeBuild("build-logic")

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		mavenLocal()
		maven { url = uri("https://jitpack.io") }
	}
}

rootProject.name = "SLE-android-app"

include(":app")

include(":core:ktx")
include(":core:base")
include(":core:i18n")
include(":core:network")
include(":core:settings")

include(":ui:main")
include(":ui:base")
include(":ui:theme")
include(":ui:dragon")
