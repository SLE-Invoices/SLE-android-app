package org.elnix.sle.ui.components

import androidx.compose.runtime.Composable
import org.elnix.sle.i18n.R

@Composable
fun ResetIcon(enabled: Boolean = true, onReset: () -> Unit) {
	DragonIconButton(
		icon = R.drawable.reset,
		contentDescription = R.string.reset,
		enabled = enabled,
		onClick = onReset
	)
}

@Composable
fun CopyIcon(enabled: Boolean = true, onCopy: () -> Unit) {
	DragonIconButton(
		icon = R.drawable.copy,
		contentDescription = R.string.copy,
		enabled = enabled,
		onClick = onCopy
	)
}
