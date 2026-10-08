package org.elnix.sle.network

/**
 * Erreurs levées par [RentalApi].
 *
 * Chaque sous-classe correspond à un message utilisateur différent (voir
 * `R.string.error_*` dans `:core:i18n`).
 */
sealed class RentalApiException(
	message: String,
	cause: Throwable? = null
) : Exception(message, cause) {
	/**
	 * L'URL configurée dans les réglages n'est pas une URL HTTP valide.
	 *
	 * @property url URL fautive, à afficher au manager pour corriger les réglages.
	 */
	class InvalidUrl(
		val url: String
	) : RentalApiException("Invalid server URL: $url")

	/**
	 * Le serveur n'a pas répondu (éteint, hors réseau, délai dépassé...).
	 *
	 * @property cause Erreur d'entrée/sortie d'OkHttp.
	 */
	class Unreachable(
		cause: Throwable
	) : RentalApiException("Server unreachable", cause)

	/**
	 * Le serveur a répondu avec un code HTTP d'erreur (4xx/5xx).
	 *
	 * @property statusCode Code HTTP retourné par le serveur.
	 */
	class HttpError(
		val statusCode: Int
	) : RentalApiException("Server answered with HTTP $statusCode")

	/**
	 * La réponse du serveur n'est pas le JSON attendu.
	 *
	 * @property cause Cause de l'échec de décodage, si connue.
	 */
	class InvalidResponse(
		cause: Throwable? = null
	) : RentalApiException("Invalid server response", cause)

	/**
	 * Le contrat n'a pas pu être encodé en JSON.
	 *
	 * @property cause Cause de l'échec d'encodage, si connue.
	 */
	class EncodingError(
		cause: Throwable? = null
	) : RentalApiException("Contract could not be encoded", cause)
}
