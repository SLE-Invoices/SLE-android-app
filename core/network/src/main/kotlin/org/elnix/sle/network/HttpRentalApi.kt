package org.elnix.sle.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.elnix90.logging.LogTag
import io.github.elnix90.logging.logE
import io.github.elnix90.logging.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.elnix.sle.base.DragonJson
import org.elnix.sle.settings.stores.map.ApiSettingsStore
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val NETWORK_TAG: LogTag = LogTag("RentalApi")

private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

/** Instance [DragonJson] partagée pour encoder/decoder les contrats avec les logs du projet. */
private val apiJson = object : DragonJson<Nothing>() {}

/**
 * Implémentation HTTP de [RentalApi].
 *
 * Le contrat est POSTé en JSON sur l'URL construite à partir de `ApiSettingsStore`
 * (URL de base, chemin, délai d'attente), relue à chaque appel : le manager peut donc
 * changer de serveur sans redémarrer l'application.
 *
 * Toutes les opérations réseau sont exécutées sur [Dispatchers.IO].
 *
 * @param ctx Contexte applicatif utilisé pour lire les réglages.
 * @param client Client OkHttp partagé (fourni par [NetworkModule]).
 */
internal class HttpRentalApi
	@Inject
	constructor(
		@ApplicationContext
		private val ctx: Context,
		private val client: OkHttpClient
	) : RentalApi {
		override suspend fun submit(contract: RentalContract): Result<RentalContractResponse> = withContext(Dispatchers.IO) {
			val baseUrl = ApiSettingsStore.apiBaseUrl.get(ctx).trimEnd('/')
			val path = ApiSettingsStore.apiPath.get(ctx)
			val timeoutSeconds = ApiSettingsStore.apiTimeoutSeconds.get(ctx)
			val url = "$baseUrl$path"

			val httpUrl = url.toHttpUrlOrNull()
				?: return@withContext Result.failure(RentalApiException.InvalidUrl(url))

			val payload = apiJson.encode(contract)
				?: return@withContext Result.failure(RentalApiException.EncodingError())

			val request = Request
				.Builder()
				.url(httpUrl)
				.post(payload.toRequestBody(JSON_MEDIA_TYPE))
				.build()

			// Un client dérivé garde le pool de connexions tout en appliquant le délai des réglages.
			val call = client
				.newBuilder()
				.callTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
				.build()

			try {
				call.newCall(request).execute().use { response ->
					if (!response.isSuccessful) {
						logI(NETWORK_TAG) { "POST $url -> HTTP ${response.code}" }
						return@withContext Result.failure(RentalApiException.HttpError(response.code))
					}

					val decoded = apiJson.decode<RentalContractResponse>(response.body.string())
						?: return@withContext Result.failure(RentalApiException.InvalidResponse())

					logI(NETWORK_TAG) { "POST $url -> HTTP ${response.code}, contract ${decoded.id}" }
					Result.success(decoded)
				}
			} catch (e: IOException) {
				logE(NETWORK_TAG, e) { "POST $url could not reach the server" }
				Result.failure(RentalApiException.Unreachable(e))
			} catch (e: IllegalArgumentException) {
				logE(NETWORK_TAG, e) { "POST $url is misconfigured" }
				Result.failure(RentalApiException.InvalidUrl(url))
			}
		}
	}
