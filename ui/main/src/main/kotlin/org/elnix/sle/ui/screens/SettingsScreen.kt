package org.elnix.sle.ui.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.elnix.sle.base.utils.CopyPasteUtils.copyToClipboard
import org.elnix.sle.i18n.R
import org.elnix.sle.settings.stores.map.ApiSettingsStore
import org.elnix.sle.settings.stores.map.BehaviorSettingsStore
import org.elnix.sle.settings.stores.map.DebugSettingsStore
import org.elnix.sle.ui.components.DragonSettingsGroup
import org.elnix.sle.ui.components.settings.SettingsItem
import org.elnix.sle.ui.components.settings.SettingsScaffold
import org.elnix.sle.ui.settings.Setting

/**
 * Écran des réglages (accessible après le PIN de [SettingsLockScreen]).
 *
 * Regroupe le serveur API, les réglages généraux du magasin et le mode débogage.
 */
@Composable
fun SettingsScreen() {
	val ctx = LocalContext.current
	val version = remember(ctx) { appVersion(ctx) }

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
}

/**
 * Lit la version de l'application installée (affichée dans « À propos »).
 *
 * @param ctx Contexte applicatif.
 * @return La version, ou `?` si elle est indisponible.
 */
private fun appVersion(ctx: Context): String = runCatching {
	ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
}.getOrNull() ?: "?"
