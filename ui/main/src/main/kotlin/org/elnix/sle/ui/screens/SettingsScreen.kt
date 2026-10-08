package org.elnix.sle.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.elnix.sle.base.utils.CopyPasteUtils.copyToClipboard
import org.elnix.sle.i18n.R
import org.elnix.sle.settings.stores.map.ApiSettingsStore
import org.elnix.sle.settings.stores.map.BehaviorSettingsStore
import org.elnix.sle.settings.stores.map.DebugSettingsStore
import org.elnix.sle.ui.components.DragonSettingsGroup
import org.elnix.sle.ui.components.settings.SettingsItem
import org.elnix.sle.ui.components.settings.SettingsScaffold
import org.elnix.sle.ui.settings.Setting
import org.elnix.sle.ui.viewmodel.ConnectionFeedback
import org.elnix.sle.ui.viewmodel.SettingsViewModel

/**
 * Settings screen (unlocked through [SettingsLockScreen]).
 *
 * Groups the API server settings — including the connectivity check that verifies
 * the configured URL actually answers — the general shop settings and the debug mode.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
	val ctx = LocalContext.current
	val version = remember(ctx) { appVersion(ctx) }

	val snackbarHostState = remember { SnackbarHostState() }
	val connectionFeedback by viewModel.feedback.collectAsStateWithLifecycle()
	val isTesting by viewModel.isTesting.collectAsStateWithLifecycle()

	// Resolved during composition (not inside the coroutine) so `stringResource`
	// stays configuration-aware (live language change).
	val testText = when (val current = connectionFeedback) {
		null -> {
			null
		}

		is ConnectionFeedback.Success -> {
			stringResource(
				R.string.test_connection_success,
				current.url,
				current.health.resolvedHost ?: current.url,
				current.health.version
			)
		}

		is ConnectionFeedback.Failure -> {
			val reason = if (current.arg == null) {
				stringResource(current.message)
			} else {
				stringResource(current.message, current.arg)
			}
			stringResource(R.string.test_connection_failed, current.url, reason)
		}
	}

	LaunchedEffect(testText) {
		if (testText != null) {
			snackbarHostState.showSnackbar(testText)
			viewModel.consumeFeedback()
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		SettingsScaffold(stringResource(R.string.settings)) {
			DragonSettingsGroup(R.string.server_settings) {
				Setting(
					setting = ApiSettingsStore.apiBaseUrl,
					singleChar = false,
					singleLine = true
				)

				Setting(
					setting = ApiSettingsStore.apiPath,
					singleChar = false,
					singleLine = true
				)

				Setting(setting = ApiSettingsStore.apiTimeoutSeconds)

				SettingsItem(
					title = stringResource(R.string.test_connection),
					description = stringResource(
						if (isTesting) {
							R.string.test_connection_testing
						} else {
							R.string.test_connection_desc
						}
					),
					icon = R.drawable.check,
					enabled = !isTesting,
					onClick = { viewModel.testConnection() }
				)
			}

			DragonSettingsGroup(R.string.general_settings) {
				Setting(setting = BehaviorSettingsStore.keepScreenOn)
			}

			DragonSettingsGroup(R.string.debug_settings) {
				Setting(setting = DebugSettingsStore.debugEnabled)
				Setting(setting = DebugSettingsStore.enableLogging)
			}

			DragonSettingsGroup(R.string.about_settings) {
				SettingsItem(
					title = stringResource(R.string.version),
					description = version,
					icon = R.drawable.help,
					onClick = { ctx.copyToClipboard(version) }
				)
			}
		}

		SnackbarHost(
			hostState = snackbarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(16.dp)
		)
	}
}

/**
 * Reads the installed application version (shown under "About").
 *
 * @param ctx Application context.
 * @return The version, or `?` when unavailable.
 */
private fun appVersion(ctx: Context): String = runCatching {
	ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
}.getOrNull() ?: "?"
