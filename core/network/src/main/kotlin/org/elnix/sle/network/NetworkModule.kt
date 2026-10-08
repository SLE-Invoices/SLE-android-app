package org.elnix.sle.network

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt providers for the network module.
 *
 * - A shared singleton [OkHttpClient] (reused connection pool).
 * - The binding of the [RentalApi] interface to [HttpRentalApi].
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {
	/**
	 * Binds the [RentalApi] interface to its HTTP implementation [HttpRentalApi].
	 *
	 * This binding is what allows [org.elnix.sle.ui.viewmodel.MainViewModel]
	 * to inject [RentalApi] directly.
	 *
	 * @param impl Concrete implementation provided by Hilt.
	 * @return The contract submission service.
	 */
	@Binds
	abstract fun bindRentalApi(impl: HttpRentalApi): RentalApi

	internal companion object {
		/**
		 * HTTP client shared by all requests.
		 *
		 * Per-call timeouts (the "timeout" setting) are applied by
		 * [HttpRentalApi] on a derived client.
		 *
		 * @return The configured client.
		 */
		@Provides
		@Singleton
		fun provideOkHttpClient(): OkHttpClient =
			OkHttpClient
				.Builder()
				.connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.build()
	}
}

private const val CONNECT_TIMEOUT_SECONDS: Long = 10
private const val READ_TIMEOUT_SECONDS: Long = 30
