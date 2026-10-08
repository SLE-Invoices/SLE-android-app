package org.elnix.sle.network

/**
 * Errors thrown by [RentalApi].
 *
 * Each subclass corresponds to a different user-facing message (see
 * `R.string.error_*` in `:core:i18n`).
 */
sealed class RentalApiException(
	message: String,
	cause: Throwable? = null
) : Exception(message, cause) {
	/**
	 * The URL configured in the settings is not a valid HTTP URL.
	 *
	 * @property url The offending URL, to show to the manager so the settings can be fixed.
	 */
	class InvalidUrl(
		val url: String
	) : RentalApiException("Invalid server URL: $url")

	/**
	 * The server did not respond (powered off, off the network, timeout exceeded...).
	 *
	 * @property cause The OkHttp I/O error.
	 */
	class Unreachable(
		cause: Throwable
	) : RentalApiException("Server unreachable", cause)

	/**
	 * The server responded with an HTTP error code (4xx/5xx).
	 *
	 * @property statusCode The HTTP code returned by the server.
	 */
	class HttpError(
		val statusCode: Int
	) : RentalApiException("Server answered with HTTP $statusCode")

	/**
	 * The server response is not the expected JSON.
	 *
	 * @property cause The cause of the decoding failure, if known.
	 */
	class InvalidResponse(
		cause: Throwable? = null
	) : RentalApiException("Invalid server response", cause)

	/**
	 * The contract could not be encoded as JSON.
	 *
	 * @property cause The cause of the encoding failure, if known.
	 */
	class EncodingError(
		cause: Throwable? = null
	) : RentalApiException("Contract could not be encoded", cause)
}
