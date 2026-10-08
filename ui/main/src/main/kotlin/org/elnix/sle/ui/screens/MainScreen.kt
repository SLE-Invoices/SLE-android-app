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
 * Main screen: the rental form filled in by the customer.
 *
 * The customer enters their details, confirms, and the contract is sent to the server via
 * [MainViewModel]. On success the form is reset and a snackbar
 * confirms the submission.
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

	// Resolved during composition (not in the coroutine): `stringResource`
	// remains configuration-sensitive (hot language change).
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
 * Form text field, with a label, adapted keyboard and error message.
 *
 * @param value Current text of the field.
 * @param onValueChange Notified on every keystroke.
 * @param label Label resource (`R.string.*`).
 * @param modifier Modifier (weight within a [Row] in particular).
 * @param error Error message resource, or `null` if the field is valid.
 * @param keyboardType Numeric keyboard type.
 * @param imeAction Action of the keyboard's Enter key.
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
 * Ski type selector, presented as clickable pills (tablet use).
 *
 * @param selected Currently selected type.
 * @param onSelect Notified when the user picks a different type.
 * @param modifier Modifier of the container.
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

/** French/English label resource associated with a [SkiType]. */
private val SkiType.labelRes: Int
	get() = when (this) {
		SkiType.ALPINE -> R.string.ski_type_alpine
		SkiType.SNOWBOARD -> R.string.ski_type_snowboard
		SkiType.CROSS_COUNTRY -> R.string.ski_type_cross_country
		SkiType.TOURING -> R.string.ski_type_touring
		SkiType.SNOWSHOES -> R.string.ski_type_snowshoes
	}
