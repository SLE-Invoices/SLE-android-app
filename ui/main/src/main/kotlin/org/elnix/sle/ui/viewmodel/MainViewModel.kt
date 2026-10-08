package org.elnix.sle.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.elnix.sle.i18n.R
import org.elnix.sle.network.RentalApi
import org.elnix.sle.network.RentalApiException
import org.elnix.sle.network.RentalContract
import org.elnix.sle.network.RentalLimits
import org.elnix.sle.network.SkiType
import javax.inject.Inject

/**
 * Rental form fields, used to attach a validation error
 * to a field displayed in [org.elnix.sle.ui.screens.MainScreen].
 */
enum class RentalField {
	RENTER_NAME,
	CUSTOMER_NAME,
	EMAIL,
	PHONE,
	WEIGHT_KG,
	AGE,
	SKI_LENGTH_CM,
	DIN_SETTING,
	BOOT_SIZE
}

/**
 * Rental form state.
 *
 * Numeric values are kept as text: that is what the user types
 * (French decimal comma accepted); conversion happens during validation with
 * [RentalLimits].
 *
 * @property renterName Name of the renter (the shop).
 * @property customerName Name of the customer.
 * @property email Customer's e-mail.
 * @property phone Customer's phone number.
 * @property weightKg Customer's weight in kg (entered text).
 * @property age Customer's age (entered text).
 * @property skiType Selected ski type.
 * @property skiLengthCm Ski length in cm (entered text).
 * @property dinSetting DIN setting (entered text).
 * @property bootSize Boot size (entered text).
 * @property fieldErrors Validation errors per field, keyed by [RentalField] and
 * pointing to an `R.string.error_*` resource.
 * @property isSubmitting `true` while submitting to the server.
 */
data class RentalFormState(
	val renterName: String = "",
	val customerName: String = "",
	val email: String = "",
	val phone: String = "",
	val weightKg: String = "",
	val age: String = "",
	val skiType: SkiType = SkiType.ALPINE,
	val skiLengthCm: String = "",
	val dinSetting: String = "",
	val bootSize: String = "",
	val fieldErrors: Map<RentalField, Int> = emptyMap(),
	val isSubmitting: Boolean = false
)

/**
 * Message to show to the user after a submission attempt.
 */
sealed interface RentalFeedback {
	/** The contract was sent successfully: the form is reset. */
	data object Sent : RentalFeedback

	/**
	 * The submission failed.
	 *
	 * @property message `R.string.*` resource to display.
	 * @property arg Optional formatting argument (e.g. HTTP status code).
	 */
	data class Failure(
		@get:StringRes val message: Int,
		val arg: Any? = null
	) : RentalFeedback
}

/**
 * ViewModel for the rental form: local validation followed by submission to the server
 * via [RentalApi].
 *
 * @property rentalApi Contract submission client (Hilt).
 */
@HiltViewModel
class MainViewModel
	@Inject
	constructor(
		private val rentalApi: RentalApi
	) : ViewModel() {
		private val formState = MutableStateFlow(RentalFormState())

		/** Observable form state, read by the UI. */
		val state: StateFlow<RentalFormState> = formState

		private val feedbackChannel = Channel<RentalFeedback>(Channel.BUFFERED)

		/** One-shot events (snackbar): collect them in a UI coroutine. */
		val feedback: Flow<RentalFeedback> = feedbackChannel.receiveAsFlow()

		/**
		 * Applies a change to the form and clears the displayed errors,
		 * since the input has just changed.
		 *
		 * @param transform Pure function that transforms the current state.
		 */
		fun update(transform: (RentalFormState) -> RentalFormState) {
			formState.value = transform(formState.value).copy(fieldErrors = emptyMap())
		}

		/** Resets the form (Cancel button and successful submission). */
		fun reset() {
			formState.value = RentalFormState()
		}

		/**
		 * Validates the form and then submits the contract to the server.
		 *
		 * If validation fails, [RentalFormState.fieldErrors] is filled and nothing is
		 * sent. Otherwise the UI switches to [RentalFormState.isSubmitting] while waiting
		 * for the response, then a [RentalFeedback] is emitted.
		 */
		fun submit() {
			val current = formState.value
			val errors = validate(current)
			if (errors.isNotEmpty()) {
				formState.value = current.copy(fieldErrors = errors)
				return
			}

			// Validation guarantees that converting the fields succeeds.
			val contract = buildContract(current) ?: return

			formState.value = current.copy(fieldErrors = emptyMap(), isSubmitting = true)
			viewModelScope.launch {
				val result = rentalApi.submit(contract)
				formState.value = formState.value.copy(isSubmitting = false)

				result.fold(
					onSuccess = {
						reset()
						feedbackChannel.send(RentalFeedback.Sent)
					},
					onFailure = { error ->
						feedbackChannel.send(error.toFeedback())
					}
				)
			}
		}

		/**
		 * Validates every form field.
		 *
		 * @param form Current form state.
		 * @return The errors per field, empty if the form is valid.
		 */
		private fun validate(form: RentalFormState): Map<RentalField, Int> {
			val errors = mutableMapOf<RentalField, Int>()

			validateName(form.renterName, RentalField.RENTER_NAME, errors)
			validateName(form.customerName, RentalField.CUSTOMER_NAME, errors)

			when {
				form.email.isBlank() -> errors[RentalField.EMAIL] = R.string.error_required
				!RentalLimits.EMAIL_PATTERN.matches(form.email.trim()) -> errors[RentalField.EMAIL] = R.string.error_invalid_email
			}

			when {
				form.phone.isBlank() -> errors[RentalField.PHONE] = R.string.error_required
				!RentalLimits.PHONE_PATTERN.matches(form.phone.trim()) -> errors[RentalField.PHONE] = R.string.error_invalid_format
			}

			validateDecimal(form.weightKg, RentalField.WEIGHT_KG, errors, RentalLimits.WEIGHT_MIN_KG, RentalLimits.WEIGHT_MAX_KG)
			validateInteger(form.age, RentalField.AGE, errors, RentalLimits.AGE_MIN, RentalLimits.AGE_MAX)
			validateInteger(form.skiLengthCm, RentalField.SKI_LENGTH_CM, errors, RentalLimits.SKI_LENGTH_MIN_CM, RentalLimits.SKI_LENGTH_MAX_CM)
			validateDecimal(form.dinSetting, RentalField.DIN_SETTING, errors, RentalLimits.DIN_MIN, RentalLimits.DIN_MAX)
			validateInteger(form.bootSize, RentalField.BOOT_SIZE, errors, RentalLimits.BOOT_SIZE_MIN, RentalLimits.BOOT_SIZE_MAX)

			return errors
		}

		private fun validateName(value: String, field: RentalField, errors: MutableMap<RentalField, Int>) {
			val length = value.trim().length
			when {
				length < RentalLimits.NAME_MIN_LENGTH -> errors[field] = R.string.error_required
				length > RentalLimits.NAME_MAX_LENGTH -> errors[field] = R.string.error_out_of_range
			}
		}

		private fun validateDecimal(
			value: String,
			field: RentalField,
			errors: MutableMap<RentalField, Int>,
			min: Double,
			max: Double
		) {
			if (value.isBlank()) {
				errors[field] = R.string.error_required
				return
			}

			val parsed = RentalLimits.parseDecimalOrNull(value)
			when (parsed) {
				null -> errors[field] = R.string.error_invalid_number
				!in min..max -> errors[field] = R.string.error_out_of_range
			}
		}

		private fun validateInteger(
			value: String,
			field: RentalField,
			errors: MutableMap<RentalField, Int>,
			min: Int,
			max: Int
		) {
			if (value.isBlank()) {
				errors[field] = R.string.error_required
				return
			}

			val parsed = RentalLimits.parseIntOrNull(value)
			when (parsed) {
				null -> errors[field] = R.string.error_invalid_number
				!in min..max -> errors[field] = R.string.error_out_of_range
			}
		}

		/**
		 * Converts the state (texts) into a [RentalContract].
		 *
		 * @param form Form state, assumed to be already validated.
		 * @return The contract, or `null` if a numeric field cannot be parsed.
		 */
		private fun buildContract(form: RentalFormState): RentalContract? {
			val weightKg = RentalLimits.parseDecimalOrNull(form.weightKg) ?: return null
			val age = RentalLimits.parseIntOrNull(form.age) ?: return null
			val skiLengthCm = RentalLimits.parseIntOrNull(form.skiLengthCm) ?: return null
			val dinSetting = RentalLimits.parseDecimalOrNull(form.dinSetting) ?: return null
			val bootSize = RentalLimits.parseIntOrNull(form.bootSize) ?: return null

			return RentalContract(
				renterName = form.renterName.trim(),
				customerName = form.customerName.trim(),
				email = form.email.trim(),
				phone = form.phone.trim(),
				weightKg = weightKg,
				age = age,
				skiType = form.skiType,
				skiLengthCm = skiLengthCm,
				dinSetting = dinSetting,
				bootSize = bootSize
			)
		}
	}

/**
 * Converts a submission error into a user-facing message.
 *
 * Shared with `SettingsViewModel`, which reports the outcome of the connection test.
 *
 * @return The [RentalFeedback] corresponding to this error.
 */
internal fun Throwable.toFeedback(): RentalFeedback = when (this) {
	is RentalApiException.InvalidUrl -> RentalFeedback.Failure(R.string.error_invalid_url)
	is RentalApiException.Unreachable -> RentalFeedback.Failure(R.string.error_server_unreachable)
	is RentalApiException.HttpError -> RentalFeedback.Failure(R.string.error_server_response, statusCode)
	is RentalApiException.InvalidResponse -> RentalFeedback.Failure(R.string.error_invalid_response)
	is RentalApiException.EncodingError -> RentalFeedback.Failure(R.string.submit_failed, message ?: "?")
	else -> RentalFeedback.Failure(R.string.submit_failed, message ?: "?")
}
