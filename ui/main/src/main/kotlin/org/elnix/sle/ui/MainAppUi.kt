package org.elnix.sle.ui

import android.annotation.SuppressLint
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.elnix.sle.base.navigaton.NavigationRoute
import org.elnix.sle.theme.SleTheme
import org.elnix.sle.ui.base.Navigator
import org.elnix.sle.ui.base.animation.horizontalMetadata
import org.elnix.sle.ui.base.animation.verticalMetadata
import org.elnix.sle.ui.base.compositionlocals.LocalNavigator
import org.elnix.sle.ui.screens.MainScreen
import org.elnix.sle.ui.screens.SettingsLockScreen
import org.elnix.sle.ui.screens.SettingsScreen

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun MainAppUi() {
	val startScreen = NavigationRoute.Main
	val backStack = rememberNavBackStack(startScreen)

	/**
	 * Settings lock: re-armed as soon as the settings screen is left,
	 * so the PIN is requested again on the next opening.
	 */
	var settingsUnlocked by retain { mutableStateOf(false) }

	val navigator: Navigator = object : Navigator {
		override fun navigate(screen: NavigationRoute) {
			if (screen is NavigationRoute.Settings) settingsUnlocked = false
			backStack.remove(screen)
			backStack.add(screen)
		}

		override fun onBack() {
			// Popping the only screen will crash so this avoids it
			if (backStack.size == 1) return
			if (backStack.lastOrNull() is NavigationRoute.Settings) settingsUnlocked = false
			backStack.removeLastOrNull()
		}

		override fun popBackMainScreen() {
			settingsUnlocked = false
			backStack.clear()
			backStack.add(NavigationRoute.Main)
		}
	}

	CompositionLocalProvider(
		LocalNavigator provides navigator
	) {
		SleTheme {
			Scaffold(
				contentWindowInsets = WindowInsets(),
				containerColor = MaterialTheme.colorScheme.background
			) { paddingValues ->

				NavDisplay(
					backStack = backStack,
					modifier = Modifier.padding(paddingValues),
					onBack = { navigator.onBack() },
					entryDecorators = listOf(
						rememberSaveableStateHolderNavEntryDecorator(),
						rememberViewModelStoreNavEntryDecorator()
					),
					predictivePopTransitionSpec = {
						ContentTransform(
							fadeIn(),
							slideOutHorizontally { it }
						)
					},
					popTransitionSpec = {
						ContentTransform(
							fadeIn(),
							slideOutHorizontally { it }
						)
					},
					entryProvider = entryProvider {
						entry<NavigationRoute.Main>(metadata = verticalMetadata) { MainScreen() }
						entry<NavigationRoute.Settings>(metadata = horizontalMetadata) {
							if (settingsUnlocked) {
								SettingsScreen()
							} else {
								SettingsLockScreen(
									onUnlocked = { settingsUnlocked = true },
									onBack = { navigator.onBack() }
								)
							}
						}
					}
				)
			}
		}
	}
}
