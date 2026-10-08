package org.elnix.sle.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.elnix.sle.i18n.R
import org.elnix.sle.network.RentalApi
import org.elnix.sle.network.ServerHealth
import javax.inject.Inject

/**
 * Outcome of a "Test connection" run, ready to be rendered by the UI.
 *
 * @property Success The server answered `GET /health`.
 * @property Failure The server could not be reached, or answered unexpectedly.
 * @property Success.health Payload returned by the server.
 * @property Success.url The URL that was actually tested.
 * @property Failure.message Resource (`R.string.error_*`) describing the failure.
 * @property Failure.arg Optional formatting argument for [Failure.message] (e.g. HTTP status).
 * @property Failure.url The URL that was actually tested.
 */
sealed interface ConnectionFeedback {
	data class Success(
		val health: ServerHealth,
		val url: String
	) : ConnectionFeedback

	data class Failure(
		@get:StringRes val message: Int,
		val arg: Any? = null,
		val url: String
	) : ConnectionFeedback
}

/**
 * ViewModel backing the settings screen: runs the connectivity check against the
 * server URL configured in `ApiSettingsStore`.
 *
 * The check exists because a wrong URL (for instance the emulator-only default
 * `10.0.2.2`) otherwise only shows up as a silent timeout when submitting a contract.
 *
 * @property rentalApi Client used to issue `GET /health` (Hilt).
 */
@HiltViewModel
class SettingsViewModel
	@Inject
	constructor(
		private val rentalApi: RentalApi
	) : ViewModel() {
		private val isTestingFlow = MutableStateFlow(false)

		/** `true` while the check is running; used to disable the button. */
		val isTesting: StateFlow<Boolean> = isTestingFlow

		private val feedbackFlow = MutableStateFlow<ConnectionFeedback?>(null)

		/** Result of the last check, `null` before running one or once it has been consumed. */
		val feedback: StateFlow<ConnectionFeedback?> = feedbackFlow

		/**
		 * Runs `GET /health` against the configured base URL.
		 *
		 * Does nothing if a check is already in flight.
		 */
		fun testConnection() {
			if (isTestingFlow.value) return

			isTestingFlow.value = true
			feedbackFlow.value = null

			viewModelScope.launch {
				// Read once so the reported URL is exactly the one that was hit.
				val url = rentalApi.healthUrl()
				val result = rentalApi.health()

				isTestingFlow.value = false
				feedbackFlow.value = result.fold(
					onSuccess = { health ->
						ConnectionFeedback.Success(health = health, url = url)
					},
					onFailure = { error ->
						when (val feedback = error.toFeedback()) {
							is RentalFeedback.Failure -> {
								ConnectionFeedback.Failure(
									message = feedback.message,
									arg = feedback.arg,
									url = url
								)
							}

							RentalFeedback.Sent -> {
								// Unreachable in practice: submit() never maps to Sent.
								ConnectionFeedback.Failure(
									message = R.string.submit_failed,
									arg = error.message,
									url = url
								)
							}
						}
					}
				)
			}
		}

		/** Clears the result once it has been shown, so it is not replayed on recomposition. */
		fun consumeFeedback() {
			feedbackFlow.value = null
		}
	}
