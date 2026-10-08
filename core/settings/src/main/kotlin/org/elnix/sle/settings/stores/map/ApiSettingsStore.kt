package org.elnix.sle.settings.stores.map

import io.github.elnix90.annotations.SettingKey
import io.github.elnix90.annotations.SettingsStore
import io.github.elnix90.core.objects.int
import io.github.elnix90.core.objects.string
import io.github.elnix90.core.stores.MapSettingsStore
import org.elnix.sle.i18n.R

/**
 * Réglages du serveur API (contrats de location).
 *
 * Ils sont lus à chaque envoi par `HttpRentalApi`, le manager peut donc corriger
 * l'adresse du serveur sans redémarrer l'application.
 */
@SettingsStore
object ApiSettingsStore : MapSettingsStore() {
	/**
	 * Adresse de base du serveur, sans slash final.
	 *
	 * La valeur par défaut pointe vers l'hôte de l'émulateur (`10.0.2.2`) pour le
	 * développement ; sur une tablette du magasin, renseigner l'IP du serveur
	 * (par exemple `http://192.168.1.10:8000`).
	 */
	@SettingKey
	val apiBaseUrl = string(
		default = "http://10.0.2.2:8000",
		title = R.string.api_base_url,
		description = R.string.api_base_url_desc
	)

	/** Chemin de l'endpoint qui reçoit les contrats (`POST`). */
	@SettingKey
	val apiPath = string(
		default = "/api/v1/rentals",
		title = R.string.api_path,
		description = R.string.api_path_desc
	)

	/** Délai maximal d'attente d'une requête, en secondes. */
	@SettingKey
	val apiTimeoutSeconds = int(
		default = 10,
		allowedRange = 1..120,
		title = R.string.api_timeout,
		description = R.string.api_timeout_desc
	)
}
