package org.elnix.sle.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload returned by `GET /health` on the server.
 *
 * [resolvedHost] is **never** sent by the server: it is filled in client-side
 * after a successful call so the UI can show which address actually answered.
 *
 * @property status Health status reported by the server (`"ok"`).
 * @property version Server package version, useful to confirm which server answered.
 * @property resolvedHost Best-effort IP address [host] resolved to, or `null`.
 */
@Serializable
data class ServerHealth(
	val status: String = "",
	val version: String = "",
	@SerialName("resolvedHost")
	val resolvedHost: String? = null
)

/**
 * Service for submitting rental contracts to the server.
 *
 * Implemented by [HttpRentalApi]; the interface allows providing a fake
 * implementation in tests and swapping the transport later
 * (queue, direct e-mail, etc.).
 */
interface RentalApi {
	/**
	 * Submits a contract to the server configured in `ApiSettingsStore`.
	 *
	 * This function never throws: errors are returned in the
	 * [Result] so the UI can display a clear message (server offline,
	 * bad URL, invalid response...).
	 *
	 * @param contract The contract validated on the form side.
	 * @return The received contract ([RentalContractResponse]) or the failure ([RentalApiException]).
	 */
	suspend fun submit(contract: RentalContract): Result<RentalContractResponse>

	/**
	 * Checks that the configured server URL is reachable from this device.
	 *
	 * This is what the "Test connection" button in the settings runs: it issues a
	 * `GET /health` against `ApiSettingsStore.apiBaseUrl` and reports the outcome,
	 * which makes a misconfigured URL (for instance the emulator-only
	 * `10.0.2.2`) obvious instead of leaving the user with a silent timeout.
	 *
	 * Like [submit], this never throws: failures are returned in the [Result].
	 *
	 * @return The server payload ([ServerHealth]) or the failure ([RentalApiException]).
	 */
	suspend fun health(): Result<ServerHealth>

	/**
	 * URL the next [health] call would target (`ApiSettingsStore.apiBaseUrl` + `/health`).
	 *
	 * Exposed so the settings screen can show *which* address was tested, which is
	 * the useful part when a check fails.
	 *
	 * @return The absolute URL as configured, trimmed of any trailing slash.
	 */
	suspend fun healthUrl(): String
}
