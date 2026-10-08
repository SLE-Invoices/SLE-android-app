package org.elnix.sle.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.elnix90.core.objects.BooleanSettingObject
import io.github.elnix90.runtime.asState
import kotlinx.coroutines.launch
import org.elnix.sle.i18n.R
import org.elnix.sle.ui.components.DragonGroupScope
import org.elnix.sle.ui.components.SwitchRow
import org.elnix.sle.ui.dialogs.UserValidation

@Composable
fun DragonGroupScope.Setting(
	setting: BooleanSettingObject,
	enabled: Boolean = true,
	needValidationToEnable: Boolean = false,
	needValidationToDisable: Boolean = false,
	confirmText: Int = R.string.are_you_sure,
	onCheck: ((Boolean) -> Unit)? = null
) {
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()

	val state by setting.asState()

	var showConfirmPopup by remember { mutableStateOf<Boolean?>(null) }

	fun toggle(state: Boolean) {
		onCheck?.invoke(state)
		scope.launch {
			setting.set(ctx, state)
		}
	}

	SwitchRow(
		state = state,
		title = setting.title!!,
		description = setting.description!!, // Keep the !! to enforce all boolean settings to have a desc
		icon = setting.icon,
		enabled = enabled
	) { clicked ->
		when {
			clicked && needValidationToEnable -> showConfirmPopup = true
			!clicked && needValidationToDisable -> showConfirmPopup = false
			else -> toggle(clicked)
		}
	}

	if (showConfirmPopup != null) {
		UserValidation(
			message = stringResource(confirmText),
			onDismiss = { showConfirmPopup = null }
		) {
			toggle(showConfirmPopup!!)
			showConfirmPopup = null
		}
	}
}
