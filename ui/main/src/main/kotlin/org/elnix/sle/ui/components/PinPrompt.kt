package org.elnix.sle.ui.components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.elnix90.lock.PinLock
import org.elnix.sle.i18n.R
import org.elnix.sle.ui.base.components.Spacer

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun PinPrompt(
	onDismiss: () -> Unit,
	onSuccess: () -> Unit
) {
	val haptic = LocalHapticFeedback.current

	var failedTries by remember { mutableIntStateOf(0) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	val wrongPinText = stringResource(R.string.wrong_pin)

	val horizontalOffsetError = remember { Animatable(0f) }

	LaunchedEffect(failedTries) {
		if (failedTries > 0) {
			var left = true
			repeat(5) {
				horizontalOffsetError.animateTo(
					animationSpec =
						tween(
							durationMillis = 100,
							easing = LinearEasing
						),
					targetValue =
						if (left) {
							-5f
						} else {
							5f
						}
				)
				left = !left
			}
			horizontalOffsetError.animateTo(0f)
		}
	}

	// Lock color animation system
	val defaultLockColor = MaterialTheme.colorScheme.primary
	val errorColor = MaterialTheme.colorScheme.error

	val lockColor =
		remember {
			Animatable(
				initialValue = defaultLockColor
			)
		}

	LaunchedEffect(failedTries) {
		if (failedTries > 0) {
			lockColor.animateTo(errorColor)
		}
	}

	LaunchedEffect(errorMessage) {
		if (errorMessage == null) {
			lockColor.animateTo(defaultLockColor)
		}
	}

	BackHandler {
		haptic.performHapticFeedback(HapticFeedbackType.Confirm)
		onDismiss()
	}

	Column(
		modifier =
			Modifier
				.fillMaxSize()
				.padding(20.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Bottom
	) {
		Icon(
			painter = painterResource(R.drawable.lock),
			contentDescription = null,
			tint = lockColor.value,
			modifier =
				Modifier
					.offset(x = horizontalOffsetError.value.dp)
					.size(50.dp)
		)
		Spacer(8.dp)

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

		AnimatedVisibility(errorMessage != null) {
			if (errorMessage != null) {
				Text(
					text = errorMessage!!,
					color = MaterialTheme.colorScheme.error,
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
		Spacer(40.dp)
		PinLock(
			modifier = Modifier.padding(bottom = 80.dp),
			onValidate = { pin ->
				if (pin == "1234") {
					haptic.performHapticFeedback(HapticFeedbackType.Confirm)
					onSuccess()
				} else {
					haptic.performHapticFeedback(HapticFeedbackType.Reject)
					errorMessage = wrongPinText
					failedTries++
				}
			}
		)
	}
}
