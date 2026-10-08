package org.elnix.sle.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.elnix.sle.base.navigaton.NavigationRoute
import org.elnix.sle.i18n.R
import org.elnix.sle.network.SkiType
import org.elnix.sle.ui.base.components.AnimatedFab
import org.elnix.sle.ui.base.compositionlocals.LocalNavigator
import org.elnix.sle.ui.components.ValidateCancelButtonsWithLoading
import org.elnix.sle.ui.components.settings.SettingsScaffold
import org.elnix.sle.ui.viewmodel.MainViewModel
import org.elnix.sle.ui.viewmodel.RentalFeedback
import org.elnix.sle.ui.viewmodel.RentalField

/**
 * Écran principal : formulaire de location saisi par le client.
 *
 * Le client remplit ses informations, valide, et le contrat part au serveur via
 * [MainViewModel]. En cas de succès le formulaire repart à zéro et une snackbar
 * confirme l'envoi.
 */
@Composable
fun MainScreen() {
	val viewModel: MainViewModel = hiltViewModel()
	val formState by viewModel.state.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	var pendingFeedback by remember { mutableStateOf<RentalFeedback?>(null) }

	LaunchedEffect(Unit) {
		viewModel.feedback.collect { pendingFeedback = it }
	}

	// Résolution pendant la composition (et non dans la coroutine) : `stringResource`
	// reste sensible à la configuration (changement de langue à chaud).
	val feedbackText = when (val feedback = pendingFeedback) {
		null -> {
			null
		}

		RentalFeedback.Sent -> {
			stringResource(R.string.submit_success)
		}

		is RentalFeedback.Failure -> {
			if (feedback.arg == null) {
				stringResource(feedback.message)
			} else {
				stringResource(feedback.message, feedback.arg)
			}
		}
	}

	LaunchedEffect(feedbackText) {
		if (feedbackText != null) {
			snackbarHostState.showSnackbar(feedbackText)
			pendingFeedback = null
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		SettingsScaffold(title = stringResource(R.string.main_screen)) {
			FormTextField(
				value = formState.renterName,
				onValueChange = { value -> viewModel.update { it.copy(renterName = value) } },
				label = R.string.renter_name,
				error = formState.fieldErrors[RentalField.RENTER_NAME]
			)

			FormTextField(
				value = formState.customerName,
				onValueChange = { value -> viewModel.update { it.copy(customerName = value) } },
				label = R.string.customer_name,
				error = formState.fieldErrors[RentalField.CUSTOMER_NAME]
			)

			FormTextField(
				value = formState.email,
				onValueChange = { value -> viewModel.update { it.copy(email = value) } },
				label = R.string.email,
				error = formState.fieldErrors[RentalField.EMAIL],
				keyboardType = KeyboardType.Email
			)

			FormTextField(
				value = formState.phone,
				onValueChange = { value -> viewModel.update { it.copy(phone = value) } },
				label = R.string.phone,
				error = formState.fieldErrors[RentalField.PHONE],
				keyboardType = KeyboardType.Phone
			)

			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				FormTextField(
					value = formState.weightKg,
					onValueChange = { value -> viewModel.update { it.copy(weightKg = value) } },
					label = R.string.weight_kg,
					modifier = Modifier.weight(1f),
					error = formState.fieldErrors[RentalField.WEIGHT_KG],
					keyboardType = KeyboardType.Decimal
				)

				FormTextField(
					value = formState.age,
					onValueChange = { value -> viewModel.update { it.copy(age = value) } },
					label = R.string.age,
					modifier = Modifier.weight(1f),
					error = formState.fieldErrors[RentalField.AGE],
					keyboardType = KeyboardType.Number
				)
			}

			SkiTypeSelector(
				selected = formState.skiType,
				onSelect = { type -> viewModel.update { it.copy(skiType = type) } }
			)

			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				FormTextField(
					value = formState.skiLengthCm,
					onValueChange = { value -> viewModel.update { it.copy(skiLengthCm = value) } },
					label = R.string.ski_length_cm,
					modifier = Modifier.weight(1f),
					error = formState.fieldErrors[RentalField.SKI_LENGTH_CM],
					keyboardType = KeyboardType.Number
				)

				FormTextField(
					value = formState.dinSetting,
					onValueChange = { value -> viewModel.update { it.copy(dinSetting = value) } },
					label = R.string.din_setting,
					modifier = Modifier.weight(1f),
					error = formState.fieldErrors[RentalField.DIN_SETTING],
					keyboardType = KeyboardType.Decimal
				)

				FormTextField(
					value = formState.bootSize,
					onValueChange = { value -> viewModel.update { it.copy(bootSize = value) } },
					label = R.string.boot_size,
					modifier = Modifier.weight(1f),
					error = formState.fieldErrors[RentalField.BOOT_SIZE],
					keyboardType = KeyboardType.Number,
					imeAction = ImeAction.Done
				)
			}

			ValidateCancelButtonsWithLoading(
				validateText = stringResource(R.string.validate),
				validateEnabled = !formState.isSubmitting,
				hasClickedValidate = formState.isSubmitting,
				onCancel = { viewModel.reset() },
				onConfirm = { viewModel.submit() }
			)

			val navigator = LocalNavigator.current
			AnimatedFab(
				icon = R.drawable.settings
			) {
				navigator.navigate(NavigationRoute.Settings)
			}
		}

		SnackbarHost(
			hostState = snackbarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(16.dp)
		)
	}
}

/**
 * Champ de texte du formulaire, avec libellé, clavier adapté et message d'erreur.
 *
 * @param value Texte courant du champ.
 * @param onValueChange Notifié à chaque frappe.
 * @param label Ressource du libellé (`R.string.*`).
 * @param modifier Modificateur (poids dans une [Row] notamment).
 * @param error Ressource du message d'erreur, ou `null` si le champ est valide.
 * @param keyboardType Type de clavier numérique.
 * @param imeAction Action de la touche Entrée du clavier.
 */
@Composable
private fun FormTextField(
	value: String,
	onValueChange: (String) -> Unit,
	@StringRes label: Int,
	modifier: Modifier = Modifier,
	@StringRes error: Int? = null,
	keyboardType: KeyboardType = KeyboardType.Text,
	imeAction: ImeAction = ImeAction.Next
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(stringResource(label)) },
		isError = error != null,
		supportingText = if (error == null) {
			null
		} else {
			{ Text(stringResource(error)) }
		},
		keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
		singleLine = true,
		modifier = modifier.fillMaxWidth()
	)
}

/**
 * Sélecteur du type de ski, présenté en pastilles cliquables (usage tablette).
 *
 * @param selected Type actuellement choisi.
 * @param onSelect Notifié quand l'utilisateur choisit un autre type.
 * @param modifier Modificateur du conteneur.
 */
@Composable
private fun SkiTypeSelector(
	selected: SkiType,
	onSelect: (SkiType) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Text(
			text = stringResource(R.string.ski_type),
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)

		Row(
			modifier = Modifier.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			SkiType.entries.forEach { type ->
				FilterChip(
					selected = type == selected,
					onClick = { onSelect(type) },
					label = { Text(stringResource(type.labelRes)) }
				)
			}
		}
	}
}

/** Ressource du libellé français/anglais associée à un [SkiType]. */
private val SkiType.labelRes: Int
	get() = when (this) {
		SkiType.ALPINE -> R.string.ski_type_alpine
		SkiType.SNOWBOARD -> R.string.ski_type_snowboard
		SkiType.CROSS_COUNTRY -> R.string.ski_type_cross_country
		SkiType.TOURING -> R.string.ski_type_touring
		SkiType.SNOWSHOES -> R.string.ski_type_snowshoes
	}
