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
 * Champs du formulaire de location, utilisés pour rattacher une erreur de validation
 * à un champ affiché dans [org.elnix.sle.ui.screens.MainScreen].
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
 * État du formulaire de location.
 *
 * Les valeurs numériques sont gardées en texte : c'est ce que l'utilisateur tape
 * (virgule française acceptée) ; la conversion se fait à la validation avec
 * [RentalLimits].
 *
 * @property renterName Nom du loueur (le magasin).
 * @property customerName Nom du client.
 * @property email E-mail du client.
 * @property phone Téléphone du client.
 * @property weightKg Poids du client en kg (texte saisi).
 * @property age Âge du client (texte saisi).
 * @property skiType Type de ski choisi.
 * @property skiLengthCm Longueur du ski en cm (texte saisi).
 * @property dinSetting Réglage DIN (texte saisi).
 * @property bootSize Pointure de chaussure (texte saisi).
 * @property fieldErrors Erreurs de validation par champ, clées sur [RentalField] et
 * pointant vers une ressource `R.string.error_*`.
 * @property isSubmitting `true` pendant l'envoi au serveur.
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
 * Message à afficher à l'utilisateur après une tentative d'envoi.
 */
sealed interface RentalFeedback {
	/** Le contrat a bien été envoyé : le formulaire est remis à zéro. */
	data object Sent : RentalFeedback

	/**
	 * L'envoi a échoué.
	 *
	 * @property message Ressource `R.string.*` à afficher.
	 * @property arg Argument de formatage optionnel (ex: code HTTP).
	 */
	data class Failure(
		@get:StringRes val message: Int,
		val arg: Any? = null
	) : RentalFeedback
}

/**
 * ViewModel du formulaire de location : validation locale puis envoi au serveur
 * via [RentalApi].
 *
 * @property rentalApi Client d'envoi des contrats (Hilt).
 */
@HiltViewModel
class MainViewModel
	@Inject
	constructor(
		private val rentalApi: RentalApi
	) : ViewModel() {
		private val formState = MutableStateFlow(RentalFormState())

		/** État observable du formulaire, lu par l'UI. */
		val state: StateFlow<RentalFormState> = formState

		private val feedbackChannel = Channel<RentalFeedback>(Channel.BUFFERED)

		/** Événements one-shot (snackbar) : à collecter dans une coroutine UI. */
		val feedback: Flow<RentalFeedback> = feedbackChannel.receiveAsFlow()

		/**
		 * Applique une modification au formulaire et efface les erreurs affichées,
		 * puisque la saisie vient de changer.
		 *
		 * @param transform Fonction pure qui transforme l'état courant.
		 */
		fun update(transform: (RentalFormState) -> RentalFormState) {
			formState.value = transform(formState.value).copy(fieldErrors = emptyMap())
		}

		/** Remet le formulaire à zéro (bouton Annuler et envoi réussi). */
		fun reset() {
			formState.value = RentalFormState()
		}

		/**
		 * Valide le formulaire puis envoie le contrat au serveur.
		 *
		 * Si la validation échoue, [RentalFormState.fieldErrors] est rempli et rien n'est
		 * envoyé. Sinon l'UI bascule en [RentalFormState.isSubmitting] le temps de la réponse,
		 * puis un [RentalFeedback] est émis.
		 */
		fun submit() {
			val current = formState.value
			val errors = validate(current)
			if (errors.isNotEmpty()) {
				formState.value = current.copy(fieldErrors = errors)
				return
			}

			// La validation garantit que la conversion des champs aboutit.
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
		 * Valide tous les champs du formulaire.
		 *
		 * @param form État courant du formulaire.
		 * @return Les erreurs par champ, vide si le formulaire est valide.
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
		 * Convertit l'état (textes) en [RentalContract].
		 *
		 * @param form État du formulaire, supposé déjà validé.
		 * @return Le contrat, ou `null` si un champ numérique est illisible.
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
 * Convertit une erreur d'envoi en message utilisateur.
 *
 * @return Le [RentalFeedback] correspondant à cette erreur.
 */
private fun Throwable.toFeedback(): RentalFeedback = when (this) {
	is RentalApiException.InvalidUrl -> RentalFeedback.Failure(R.string.error_invalid_url)
	is RentalApiException.Unreachable -> RentalFeedback.Failure(R.string.error_server_unreachable)
	is RentalApiException.HttpError -> RentalFeedback.Failure(R.string.error_server_response, statusCode)
	is RentalApiException.InvalidResponse -> RentalFeedback.Failure(R.string.error_invalid_response)
	is RentalApiException.EncodingError -> RentalFeedback.Failure(R.string.submit_failed, message ?: "?")
	else -> RentalFeedback.Failure(R.string.submit_failed, message ?: "?")
}
