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
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val NETWORK_TAG: LogTag = LogTag("RentalApi")

private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

/** Path of the health endpoint, appended to the configured base URL. */
private const val HEALTH_PATH = "/health"

/** Shared [DragonJson] instance used to encode/decode contracts with the project's logging. */
private val apiJson = object : DragonJson<Nothing>() {}

/**
 * HTTP implementation of [RentalApi].
 *
 * The contract is POSTed as JSON to the URL built from `ApiSettingsStore`
 * (base URL, path, timeout), re-read on every call: the manager can therefore
 * switch servers without restarting the application.
 *
 * All network operations run on [Dispatchers.IO].
 *
 * @param ctx Application context used to read the settings.
 * @param client Shared OkHttp client (provided by [NetworkModule]).
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

			// A derived client keeps the connection pool while applying the configured timeout.
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

		override suspend fun health(): Result<ServerHealth> = withContext(Dispatchers.IO) {
			val baseUrl = ApiSettingsStore.apiBaseUrl.get(ctx).trimEnd('/')
			val timeoutSeconds = ApiSettingsStore.apiTimeoutSeconds.get(ctx)
			val url = "$baseUrl$HEALTH_PATH"

			val httpUrl = url.toHttpUrlOrNull()
				?: return@withContext Result.failure(RentalApiException.InvalidUrl(url))

			val request = Request
				.Builder()
				.url(httpUrl)
				.get()
				.build()

			// Same derived-client trick as submit(): apply the configured timeout.
			val call = client
				.newBuilder()
				.callTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
				.build()

			try {
				call.newCall(request).execute().use { response ->
					if (!response.isSuccessful) {
						logI(NETWORK_TAG) { "GET $url -> HTTP ${response.code}" }
						return@withContext Result.failure(RentalApiException.HttpError(response.code))
					}

					val decoded = apiJson.decode<ServerHealth>(response.body.string())
						?: return@withContext Result.failure(RentalApiException.InvalidResponse())

					// Best effort: shows the manager which address actually answered.
					val resolvedHost = runCatching {
						InetAddress.getByName(httpUrl.host).hostAddress
					}.getOrNull()

					logI(NETWORK_TAG) { "GET $url -> HTTP ${response.code}, version ${decoded.version}" }
					Result.success(decoded.copy(resolvedHost = resolvedHost))
				}
			} catch (e: IOException) {
				logE(NETWORK_TAG, e) { "GET $url could not reach the server" }
				Result.failure(RentalApiException.Unreachable(e))
			} catch (e: IllegalArgumentException) {
				logE(NETWORK_TAG, e) { "GET $url is misconfigured" }
				Result.failure(RentalApiException.InvalidUrl(url))
			}
		}

		override suspend fun healthUrl(): String = withContext(Dispatchers.IO) {
			val baseUrl = ApiSettingsStore.apiBaseUrl.get(ctx).trimEnd('/')
			"$baseUrl$HEALTH_PATH"
		}
	}
