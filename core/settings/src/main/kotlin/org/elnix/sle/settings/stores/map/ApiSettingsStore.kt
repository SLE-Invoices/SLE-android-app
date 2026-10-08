package org.elnix.sle.settings.stores.map

import io.github.elnix90.annotations.SettingKey
import io.github.elnix90.annotations.SettingsStore
import io.github.elnix90.core.objects.int
import io.github.elnix90.core.objects.string
import io.github.elnix90.core.stores.MapSettingsStore
import org.elnix.sle.i18n.R

/**
 * API server settings (rental contracts).
 *
 * They are re-read on every submission by `HttpRentalApi`, so the manager can fix
 * the server address without restarting the application.
 */
@SettingsStore
object ApiSettingsStore : MapSettingsStore() {
	/**
	 * Base URL of the server, without a trailing slash.
	 *
	 * The default value points at the emulator host (`10.0.2.2`) for
	 * development; on an in-store tablet, set the server IP
	 * (for example `http://192.168.1.10:8000`).
	 */
	@SettingKey
	val apiBaseUrl = string(
		default = "http://10.0.2.2:8000",
		title = R.string.api_base_url,
		description = R.string.api_base_url_desc
	)

	/** Path of the endpoint that receives the contracts (`POST`). */
	@SettingKey
	val apiPath = string(
		default = "/api/v1/rentals",
		title = R.string.api_path,
		description = R.string.api_path_desc
	)

	/** Maximum time to wait for a request, in seconds. */
	@SettingKey
	val apiTimeoutSeconds = int(
		default = 10,
		allowedRange = 1..120,
		title = R.string.api_timeout,
		description = R.string.api_timeout_desc
	)
}
