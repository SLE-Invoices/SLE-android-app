package org.elnix.sle.settings.stores.map

import io.github.elnix90.annotations.SettingKey
import io.github.elnix90.annotations.SettingsStore
import io.github.elnix90.core.objects.boolean
import io.github.elnix90.core.stores.MapSettingsStore
import org.elnix.sle.i18n.R

@SettingsStore
object DebugSettingsStore : MapSettingsStore() {
	@SettingKey
	val debugEnabled = boolean(
		title = R.string.activate_debug_mode,
		description = R.string.activate_debug_mode_desc,
		default = false
	)

	@SettingKey
	val enableLogging = boolean(
		title = R.string.enable_logging,
		description = R.string.enable_logging_desc,
		default = true
	)
}
