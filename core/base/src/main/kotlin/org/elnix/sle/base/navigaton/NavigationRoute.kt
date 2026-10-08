package org.elnix.sle.base.navigaton

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.elnix.sle.i18n.R

@Serializable
sealed class NavigationRoute : NavKey {
	@get:StringRes
	abstract val resId: Int

	@get:DrawableRes
	abstract val icon: Int

	@SerialName("Main")
	@Serializable
	data object Main : NavigationRoute() {
		override val resId: Int = R.string.main_screen
		override val icon: Int = R.drawable.home
	}

	@Serializable
	@SerialName("Settings")
	data object Settings : NavigationRoute() {
		override val resId: Int = R.string.settings
		override val icon: Int = R.drawable.settings
	}
}
