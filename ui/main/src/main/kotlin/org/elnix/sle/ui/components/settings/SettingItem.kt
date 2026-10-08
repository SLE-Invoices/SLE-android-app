package org.elnix.sle.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.elnix.sle.ktx.semiTransparentIfDisabled
import org.elnix.sle.ui.base.modifiers.conditional
import org.elnix.sle.ui.sle.text.TextWithDescription

@Composable
fun SettingsItem(
	title: String,
	modifier: Modifier = Modifier,
	description: String? = null,
	enabled: Boolean = true,
	icon: Int,
	trailingIcon: Int? = null,
	onLongClick: (() -> Unit)? = null,
	onExternalClick: (() -> Unit)? = null,
	onClick: () -> Unit
) {
	Row(
		modifier = modifier
			.combinedClickable(
				enabled = enabled,
				onLongClick = onLongClick,
				onClick = onClick
			).padding(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Icon(
			painter = painterResource(icon),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(enabled)
		)

		TextWithDescription(
			text = title,
			description = description,
			modifier = Modifier.weight(1f),
			enabled = enabled
		)

		if (trailingIcon != null) {
			Icon(
				painter = painterResource(trailingIcon),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(enabled),
				modifier = Modifier
					.sizeIn(maxHeight = 25.dp)
					.conditional(onExternalClick != null) {
						clickable(onClick = onExternalClick!!)
					}
			)
		}
	}
}
