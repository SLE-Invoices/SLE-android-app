package org.elnix.sle.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.elnix90.lock.PinLock
import io.github.elnix90.lock.pin.configuration.PinLockOptions
import org.elnix.sle.i18n.R

private const val MANAGER_PIN = "1234"

/**
 * Écran de verrouillage des réglages : demande le PIN du manager avant de laisser
 * accéder à [SettingsScreen] (bibliothèque `compose-lock`).
 *
 * @param onUnlocked Appelé quand le PIN est correct.
 * @param onBack Appelé quand l'utilisateur revient en arrière (ferme les réglages).
 * @param modifier Modificateur de l'écran.
 */
@Composable
fun SettingsLockScreen(
	onUnlocked: () -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier
) {
	var showError by remember { mutableStateOf(false) }

	BackHandler(onBack = onBack)

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = stringResource(R.string.settings_locked),
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.onBackground
			)

			Text(
				text = stringResource(R.string.settings_locked_desc),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)

			if (showError) {
				Text(
					text = stringResource(R.string.wrong_pin),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error
				)
			}
		}

		PinLock(
			modifier = Modifier.fillMaxWidth(),
			pinLockOptions = PinLockOptions.defaultPinLockOptions.copy(maxChars = MANAGER_PIN.length),
			onValidate = { pin ->
				if (pin == MANAGER_PIN) {
					showError = false
					onUnlocked()
				} else {
					showError = true
				}
			}
		)
	}
}
