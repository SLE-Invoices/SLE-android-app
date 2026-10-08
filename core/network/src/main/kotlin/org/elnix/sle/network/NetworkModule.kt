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
 * Fournitures Hilt du module réseau.
 *
 * - Un [OkHttpClient] singleton partagé (pool de connexions réutilisé).
 * - La liaison de l'interface [RentalApi] vers [HttpRentalApi].
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {
	/**
	 * Lie l'interface [RentalApi] à son implémentation HTTP [HttpRentalApi].
	 *
	 * C'est cette liaison qui permet à [org.elnix.sle.ui.viewmodel.MainViewModel]
	 * d'injecter [RentalApi] directement.
	 *
	 * @param impl Implémentation concrète fournie par Hilt.
	 * @return Le service d'envoi des contrats.
	 */
	@Binds
	abstract fun bindRentalApi(impl: HttpRentalApi): RentalApi

	internal companion object {
		/**
		 * Client HTTP partagé par toutes les requêtes.
		 *
		 * Les timeouts par appel (réglage « délai d'attente ») sont appliqués par
		 * [HttpRentalApi] sur un client dérivé.
		 *
		 * @return Client configuré.
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
