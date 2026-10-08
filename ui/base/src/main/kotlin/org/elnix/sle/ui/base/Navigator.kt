package org.elnix.sle.ui.base

import org.elnix.sle.base.navigaton.NavigationRoute

interface Navigator {
	/**
	 * THe correct way to navigate between screens, handles lock screen and authentication when navigating
	 *
	 * @param screen Which screen is requested to navigate to
	 */
	fun navigate(screen: NavigationRoute)

	fun onBack()

	fun popBackMainScreen()
}
